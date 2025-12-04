package com.example.Service.Operation.controller;

import com.example.Service.Operation.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller tổng hợp để demo tất cả các tính năng
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    
    @Autowired
    private MonitoringService monitoringService;
    
    /**
     * Demo endpoint với đầy đủ monitoring
     */
    @GetMapping("/monitoring")
    public ResponseEntity<Map<String, Object>> demoMonitoring() {
        logger.info("[DEMO] Monitoring demo endpoint called");
        
        long startTime = System.currentTimeMillis();
        
        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        monitoringService.logPerformance("demo_monitoring", duration, "ms");
        monitoringService.recordBusinessMetric("demo.requests", "type", "monitoring");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Monitoring demo - check logs and metrics");
        response.put("features", new String[]{
            "Structured logging with correlation IDs",
            "Performance metrics",
            "Business metrics",
            "Request/Response tracking"
        });
        response.put("duration", duration + "ms");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Demo rate limiting - gọi nhiều lần để thấy rate limit hoạt động
     */
    @GetMapping("/rate-limit-test")
    public ResponseEntity<Map<String, Object>> demoRateLimit() {
        logger.info("[DEMO] Rate limit test endpoint called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rate limit test - try calling this endpoint multiple times quickly");
        response.put("limit", "10 requests per minute per IP");
        response.put("tip", "Call this endpoint 11+ times quickly to see rate limiting in action");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Demo circuit breaker - hướng dẫn cách test
     */
    @GetMapping("/circuit-breaker-test")
    public ResponseEntity<Map<String, Object>> demoCircuitBreaker() {
        logger.info("[DEMO] Circuit breaker test endpoint called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Circuit breaker demo");
        response.put("steps", new String[]{
            "1. POST /api/circuit-breaker/simulate-failure to enable failures",
            "2. GET /api/circuit-breaker/call multiple times to trigger circuit breaker",
            "3. Circuit breaker will open after failures",
            "4. Check /actuator/metrics/resilience4j.circuitbreaker.calls for metrics",
            "5. POST /api/circuit-breaker/disable-failure to restore service"
        });
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get summary of all features
     */
    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getFeatures() {
        Map<String, Object> features = new HashMap<>();
        
        features.put("monitoring", Map.of(
            "logs", "Structured logging with correlation IDs, performance tracking",
            "metrics", "Prometheus metrics, custom business metrics",
            "tracing", "Distributed tracing with correlation IDs across requests"
        ));
        
        features.put("rateLimiting", Map.of(
            "description", "IP-based rate limiting using Bucket4j",
            "limit", "10 requests per minute per IP",
            "metrics", "rate_limit.requests (allowed/rejected)",
            "endpoint", "/api/demo/rate-limit-test"
        ));
        
        features.put("circuitBreaker", Map.of(
            "description", "Circuit breaker pattern using Resilience4j",
            "protection", "Prevents cascading failures from external services",
            "metrics", "resilience4j.circuitbreaker.*",
            "endpoints", new String[]{
                "/api/circuit-breaker/call",
                "/api/circuit-breaker/call-timeout",
                "/api/circuit-breaker/simulate-failure"
            }
        ));
        
        features.put("endpoints", Map.of(
            "monitoring", "/actuator/prometheus, /actuator/metrics",
            "health", "/actuator/health",
            "api", "/api/hello, /api/demo/*, /api/circuit-breaker/*, /api/metrics/*"
        ));
        
        return ResponseEntity.ok(features);
    }
}

