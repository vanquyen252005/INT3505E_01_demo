package com.example.Service.Operation.controller;

import com.example.Service.Operation.service.CircuitBreakerService;
import com.example.Service.Operation.service.ExternalService;
import com.example.Service.Operation.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller demo Circuit Breaker pattern
 * Minh họa cách circuit breaker bảo vệ hệ thống khỏi external service failures
 */
@RestController
@RequestMapping("/api/circuit-breaker")
public class CircuitBreakerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
    
    @Autowired
    private CircuitBreakerService circuitBreakerService;
    
    @Autowired
    private ExternalService externalService;
    
    @Autowired
    private MonitoringService monitoringService;
    
    /**
     * Demo: Gọi external service với circuit breaker protection
     * Circuit breaker sẽ mở nếu failure rate cao
     */
    @GetMapping("/call")
    public ResponseEntity<Map<String, Object>> callExternalService(
            @RequestParam(defaultValue = "payment-service") String serviceName) {
        
        logger.info("[CIRCUIT_BREAKER_CONTROLLER] Calling external service: {}", serviceName);
        monitoringService.recordBusinessMetric("circuit_breaker.requests", "service", serviceName);
        
        long startTime = System.currentTimeMillis();
        
        try {
            String result = circuitBreakerService.callWithCircuitBreaker(serviceName);
            long duration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("service", serviceName);
            response.put("result", result);
            response.put("duration", duration + "ms");
            response.put("timestamp", System.currentTimeMillis());
            
            monitoringService.logPerformance("circuit_breaker_call", duration, "ms");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[CIRCUIT_BREAKER_CONTROLLER] Error calling service: {}", serviceName, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("service", serviceName);
            response.put("error", e.getMessage());
            response.put("duration", duration + "ms");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Demo: Gọi external service với timeout protection
     */
    @GetMapping("/call-timeout")
    public ResponseEntity<Map<String, Object>> callWithTimeout(
            @RequestParam(defaultValue = "slow-service") String serviceName) {
        
        logger.info("[CIRCUIT_BREAKER_CONTROLLER] Calling with timeout: {}", serviceName);
        
        long startTime = System.currentTimeMillis();
        
        try {
            CompletableFuture<String> future = circuitBreakerService.callWithTimeout(serviceName);
            String result = future.get();
            long duration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("service", serviceName);
            response.put("result", result);
            response.put("duration", duration + "ms");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[CIRCUIT_BREAKER_CONTROLLER] Timeout error: {}", serviceName, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "timeout");
            response.put("service", serviceName);
            response.put("error", e.getMessage());
            response.put("duration", duration + "ms");
            
            return ResponseEntity.status(504).body(response);
        }
    }
    
    /**
     * Enable failure simulation để test circuit breaker
     */
    @PostMapping("/simulate-failure")
    public ResponseEntity<Map<String, Object>> enableFailureSimulation() {
        externalService.enableFailureSimulation();
        logger.warn("[CIRCUIT_BREAKER_CONTROLLER] Failure simulation enabled");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Failure simulation enabled. Circuit breaker will open after failures.");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Disable failure simulation
     */
    @PostMapping("/disable-failure")
    public ResponseEntity<Map<String, Object>> disableFailureSimulation() {
        externalService.disableFailureSimulation();
        logger.info("[CIRCUIT_BREAKER_CONTROLLER] Failure simulation disabled");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Failure simulation disabled");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get circuit breaker status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("failureSimulationEnabled", externalService.isFailureSimulationEnabled());
        response.put("message", "Check /actuator/metrics/resilience4j.circuitbreaker.calls for detailed metrics");
        return ResponseEntity.ok(response);
    }
}

