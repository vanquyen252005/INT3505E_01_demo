package com.example.webhook.service;

import com.example.webhook.entity.DeliveryAttempt;
import com.example.webhook.entity.Subscriber;
import com.example.webhook.entity.WebhookEvent;
import com.example.webhook.repository.DeliveryAttemptRepository;
import com.example.webhook.repository.SubscriberRepository;
import com.example.webhook.repository.WebhookEventRepository;
import com.example.webhook.util.SignatureUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DispatcherService {
    private final SubscriberRepository subscriberRepo;
    private final WebhookEventRepository eventRepo;
    private final DeliveryAttemptRepository deliveryRepo;
    private final WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector())
            .build();
    private final Logger log = LoggerFactory.getLogger(DispatcherService.class);

    private ScheduledExecutorService scheduledExecutor;

    @Value("${app.dispatcher.maxAttempts:6}")
    private int maxAttempts;
    @Value("${app.dispatcher.baseDelayMs:1000}")
    private long baseDelayMs;
    @Value("${app.dispatcher.maxDelayMs:3600000}")
    private long maxDelayMs;

    @PostConstruct
    public void init() {
        scheduledExecutor = Executors.newScheduledThreadPool(8);
        // background worker to pick pending deliveries whose nextTryAt <= now
        scheduledExecutor.scheduleAtFixedRate(this::scanAndDispatch, 1, 1, TimeUnit.SECONDS);
    }

    public WebhookEvent publishEvent(String eventType, String payload) {
        WebhookEvent e = WebhookEvent.builder()
                .eventType(eventType)
                .payload(payload)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
        e = eventRepo.save(e);

        List<Subscriber> subs = subscriberRepo.findAll();
        WebhookEvent finalE = e;
        subs.stream().filter(Subscriber::isActive).forEach(sub -> {
            DeliveryAttempt d = DeliveryAttempt.builder()
                    .eventId(finalE.getId())
                    .subscriberId(sub.getId())
                    .attemptCount(0)
                    .status("PENDING")
                    .createdAt(Instant.now())
                    .nextTryAt(Instant.now())
                    .build();
            deliveryRepo.save(d);
        });

        return e;
    }

    private void scanAndDispatch() {
        try {
            List<DeliveryAttempt> list = deliveryRepo.findByStatusAndNextTryAtBefore("PENDING", Instant.now());
            for (DeliveryAttempt d : list) {
                // dispatch non-blocking via scheduledExecutor
                scheduledExecutor.execute(() -> processDelivery(d));
            }
        } catch (Exception ex) {
            log.error("scan error: {}", ex.getMessage(), ex);
        }
    }

    private void processDelivery(DeliveryAttempt delivery) {
        // reload delivery (fresh)
        Optional<DeliveryAttempt> opt = deliveryRepo.findById(delivery.getId());
        if (opt.isEmpty()) return;
        DeliveryAttempt d = opt.get();
        // check if already success (idempotency)
        if ("SUCCESS".equals(d.getStatus())) return;

        Optional<WebhookEvent> oe = eventRepo.findById(d.getEventId());
        if (oe.isEmpty()) {
            d.setStatus("FAILED");
            deliveryRepo.save(d);
            return;
        }
        WebhookEvent event = oe.get();

        Optional<Subscriber> os = subscriberRepo.findById(d.getSubscriberId());
        if (os.isEmpty()) {
            d.setStatus("FAILED");
            deliveryRepo.save(d);
            return;
        }
        Subscriber sub = os.get();

        int attempt = d.getAttemptCount() + 1;
        d.setAttemptCount(attempt);
        d.setUpdatedAt(Instant.now());
        deliveryRepo.save(d);

        String timestamp = Instant.now().toString();
        String signature = null;
        if (sub.getSecret() != null) {
            signature = SignatureUtil.computeSignature(sub.getSecret(), event.getPayload(), timestamp, event.getId().toString());
        }

        try {
            WebClient.RequestBodySpec req = webClient.post().uri(sub.getEndpoint())
                    .header("X-Event-Id", event.getId().toString())
                    .header("X-Event-Type", event.getEventType())
                    .header("X-Timestamp", timestamp)
                    .contentType(MediaType.APPLICATION_JSON);

            if (signature != null) req.header("X-Signature", signature);

            // call remote
            ResponseEntity<String> resp = req.bodyValue(event.getPayload())
                    .retrieve()
                    .toEntity(String.class)
                    .block(Duration.ofSeconds(10));

            int code = resp != null && resp.getStatusCode() != null ? resp.getStatusCode().value() : 0;
            d.setLastResponseCode(code);
            d.setLastResponseBody(resp != null ? resp.getBody() : null);

            if (code >= 200 && code < 300) {
                d.setStatus("SUCCESS");
                d.setNextTryAt(null);
                d.setUpdatedAt(Instant.now());
                deliveryRepo.save(d);

                // check if all deliveries success -> mark event COMPLETED (simplified)
                boolean anyPending = deliveryRepo.findByEventIdAndSubscriberId(event.getId(), sub.getId())
                        .stream().anyMatch(at -> !"SUCCESS".equals(at.getStatus()));
                event.setStatus("COMPLETED");
                event.setDeliveredAt(Instant.now());
                eventRepo.save(event);
                return;
            } else if (code == 410) {
                // subscriber requests removal
                sub.setActive(false);
                subscriberRepo.save(sub);
                d.setStatus("FAILED");
                deliveryRepo.save(d);
                return;
            } else {
                // retryable
                scheduleRetry(d, attempt);
            }

        } catch (Exception ex) {
            log.warn("Dispatch error to {}: {}", sub.getEndpoint(), ex.getMessage());
            d.setLastResponseBody(ex.getMessage());
            scheduleRetry(d, attempt);
        }
    }

    private void scheduleRetry(DeliveryAttempt d, int attempt) {
        if (attempt >= maxAttempts) {
            d.setStatus("FAILED");
            d.setUpdatedAt(Instant.now());
            deliveryRepo.save(d);
            return;
        }
        long delay = baseDelayMs * (1L << (attempt - 1));
        if (delay > maxDelayMs) delay = maxDelayMs;
        d.setNextTryAt(Instant.now().plusMillis(delay));
        d.setStatus("PENDING");
        d.setUpdatedAt(Instant.now());
        deliveryRepo.save(d);
    }
}
