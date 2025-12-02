package com.example.webhook.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_delivery")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAttempt {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID eventId;

    private UUID subscriberId;

    private int attemptCount = 0;

    private String status = "PENDING"; // PENDING, SUCCESS, FAILED

    private Integer lastResponseCode;

    @Column(columnDefinition = "LONGTEXT")
    private String lastResponseBody;

    private Instant nextTryAt;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}
