package com.example.webhook.repository;

import com.example.webhook.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
}
