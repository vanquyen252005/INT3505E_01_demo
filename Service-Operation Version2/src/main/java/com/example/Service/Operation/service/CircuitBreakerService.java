package com.example.Service.Operation.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service demo Circuit Breaker pattern với Resilience4j
 * Bảo vệ hệ thống khỏi cascading failures từ external services
 */
@Service
public class CircuitBreakerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerService.class);
    
    private final ExternalService externalService;
    private final Counter circuitBreakerSuccessCounter;
    private final Counter circuitBreakerFailureCounter;
    private final Counter circuitBreakerOpenCounter;
    private final Timer circuitBreakerTimer;
    
    public CircuitBreakerService(ExternalService externalService, MeterRegistry meterRegistry) {
        this.externalService = externalService;
        this.circuitBreakerSuccessCounter = Counter.builder("circuit_breaker.calls")
                .tag("status", "success")
                .description("Number of successful circuit breaker calls")
                .register(meterRegistry);
        this.circuitBreakerFailureCounter = Counter.builder("circuit_breaker.calls")
                .tag("status", "failure")
                .description("Number of failed circuit breaker calls")
                .register(meterRegistry);
        this.circuitBreakerOpenCounter = Counter.builder("circuit_breaker.state")
                .tag("state", "open")
                .description("Number of times circuit breaker opened")
                .register(meterRegistry);
        this.circuitBreakerTimer = Timer.builder("circuit_breaker.duration")
                .description("Circuit breaker call duration")
                .register(meterRegistry);
    }
    
    /**
     * Gọi external service với Circuit Breaker protection
     * Circuit breaker sẽ mở nếu failure rate > 50% trong 10 requests
     */
    @CircuitBreaker(name = "externalService", fallbackMethod = "fallbackCall")
    @Retry(name = "externalService")
    public String callWithCircuitBreaker(String serviceName) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("[CIRCUIT_BREAKER] Attempting to call external service: {}", serviceName);
            String result = externalService.callExternalApi(serviceName);
            long duration = System.currentTimeMillis() - startTime;
            circuitBreakerTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            circuitBreakerSuccessCounter.increment();
            logger.info("[CIRCUIT_BREAKER] Call successful: {}", serviceName);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long duration = System.currentTimeMillis() - startTime;
            circuitBreakerTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            circuitBreakerFailureCounter.increment();
            logger.error("[CIRCUIT_BREAKER] Call interrupted: {}", serviceName, e);
            throw new RuntimeException("Circuit breaker call interrupted", e);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            circuitBreakerTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            circuitBreakerFailureCounter.increment();
            logger.error("[CIRCUIT_BREAKER] Call failed: {}", serviceName, e);
            throw new RuntimeException("Circuit breaker call failed", e);
        }
    }
    
    /**
     * Fallback method khi circuit breaker mở hoặc service fail
     */
    public String fallbackCall(String serviceName, Exception e) {
        logger.warn("[CIRCUIT_BREAKER] Fallback method called for: {} due to: {}", serviceName, e.getMessage());
        circuitBreakerOpenCounter.increment();
        return "Fallback response: Service temporarily unavailable. Please try again later.";
    }
    
    /**
     * Gọi external service với timeout protection
     */
    @TimeLimiter(name = "externalService")
    @CircuitBreaker(name = "externalService", fallbackMethod = "fallbackCallAsync")
    public CompletableFuture<String> callWithTimeout(String serviceName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("[CIRCUIT_BREAKER] Calling with timeout: {}", serviceName);
                return externalService.callExternalApiWithTimeout(serviceName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public CompletableFuture<String> fallbackCallAsync(String serviceName, Exception e) {
        logger.warn("[CIRCUIT_BREAKER] Timeout fallback for: {}", serviceName);
        return CompletableFuture.completedFuture("Fallback: Request timeout. Service is too slow.");
    }
}

