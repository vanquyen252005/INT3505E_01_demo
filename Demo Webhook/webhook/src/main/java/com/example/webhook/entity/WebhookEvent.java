package com.example.webhook.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEvent {
    @Id
    @GeneratedValue
    private UUID id;

    private String eventType;

    @Column(columnDefinition = "LONGTEXT")
    private String payload; // JSON as text

    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, FAILED

    private Instant createdAt = Instant.now();

    private Instant deliveredAt;
}
