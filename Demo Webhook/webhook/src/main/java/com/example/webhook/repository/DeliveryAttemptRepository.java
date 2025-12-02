package com.example.webhook.repository;

import com.example.webhook.entity.DeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, UUID> {
    List<DeliveryAttempt> findByStatusAndNextTryAtBefore(String status, Instant ts);
    List<DeliveryAttempt> findByEventIdAndSubscriberId(UUID eventId, UUID subscriberId);
}
