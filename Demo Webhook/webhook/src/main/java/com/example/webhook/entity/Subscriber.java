package com.example.webhook.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "webhook_subscriber")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscriber {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String endpoint;

    /**
     * Shared secret for HMAC. In production encrypt at rest.
     */
    private String secret;

    private boolean active = true;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}
