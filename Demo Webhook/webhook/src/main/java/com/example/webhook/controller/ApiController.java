package com.example.webhook.controller;

import com.example.webhook.entity.DeliveryAttempt;
import com.example.webhook.entity.Subscriber;
import com.example.webhook.entity.WebhookEvent;
import com.example.webhook.repository.DeliveryAttemptRepository;
import com.example.webhook.repository.SubscriberRepository;
import com.example.webhook.repository.WebhookEventRepository;
import com.example.webhook.service.DispatcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final SubscriberRepository subscriberRepo;
    private final DispatcherService dispatcher;
    private final WebhookEventRepository eventRepo;
    private final DeliveryAttemptRepository deliveryRepo;

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber req) {
        req.setId(null);
        req.setCreatedAt(java.time.Instant.now());
        req.setUpdatedAt(java.time.Instant.now());
        Subscriber saved = subscriberRepo.save(req);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/subscribers")
    public List<Subscriber> listSubscribers() {
        return subscriberRepo.findAll();
    }

    @PutMapping("/subscribers/{id}")
    public ResponseEntity<Subscriber> update(@PathVariable UUID id, @RequestBody Subscriber in) {
        return subscriberRepo.findById(id).map(s -> {
            s.setName(in.getName());
            s.setEndpoint(in.getEndpoint());
            s.setSecret(in.getSecret());
            s.setActive(in.isActive());
            s.setUpdatedAt(java.time.Instant.now());
            subscriberRepo.save(s);
            return ResponseEntity.ok(s);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/subscribers/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        subscriberRepo.findById(id).ifPresent(s -> {
            s.setActive(false);
            subscriberRepo.save(s);
        });
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events")
    public ResponseEntity<WebhookEvent> publish(@RequestBody Map<String, Object> body) {
        String type = (String) body.getOrDefault("type", "generic");
        Object payloadObj = body.getOrDefault("payload", Collections.emptyMap());
        String payload;
        try {
            payload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payloadObj);
        } catch (Exception e) {
            payload = "{}";
        }
        WebhookEvent e = dispatcher.publishEvent(type, payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(e);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<WebhookEvent> getEvent(@PathVariable UUID id) {
        return eventRepo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/deliveries")
    public List<DeliveryAttempt> listDeliveries(@RequestParam Optional<UUID> eventId) {
        if (eventId.isPresent()) {
            return deliveryRepo.findAll().stream().filter(d -> d.getEventId().equals(eventId.get())).toList();
        } else {
            return deliveryRepo.findAll();
        }
    }
}
