package com.example.Service.Operation.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration cho Resilience4j Circuit Breaker
 * 
 * Note: Với resilience4j-spring-boot3 và resilience4j-micrometer,
 * metrics sẽ tự động được register và expose qua /actuator/prometheus
 * Không cần manual configuration nếu sử dụng auto-configuration
 */
@Configuration
public class Resilience4jConfig {
}

