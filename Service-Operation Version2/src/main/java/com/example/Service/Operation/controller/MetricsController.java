package com.example.Service.Operation.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    
    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    /**
     * Get all available metrics
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMetrics() {
        logger.info("[METRICS] Requesting all metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Get all meters
        Map<String, Object> meterData = meterRegistry.getMeters().stream()
                .collect(Collectors.toMap(
                    meter -> meter.getId().getName(),
                    meter -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("type", meter.getId().getType().toString());
                        data.put("tags", meter.getId().getTags());
                        return data;
                    }
                ));
        
        metrics.put("meters", meterData);
        metrics.put("totalMeters", meterRegistry.getMeters().size());
        metrics.put("message", "For detailed metrics, visit /actuator/metrics or /actuator/prometheus");
        
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Get specific metric by name
     */
    @GetMapping("/{metricName}")
    public ResponseEntity<Map<String, Object>> getMetric(@PathVariable String metricName) {
        logger.info("[METRICS] Requesting metric: {}", metricName);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Try to get the metric
            Meter meter = meterRegistry.find(metricName).meter();
            if (meter != null) {
                response.put("metricName", metricName);
                response.put("found", true);
                response.put("type", meter.getId().getType().toString());
                response.put("tags", meter.getId().getTags());
            } else {
                response.put("found", false);
                response.put("message", "Metric not found. Available metrics at /actuator/metrics");
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get monitoring information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getMonitoringInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("monitoring", Map.of(
            "logs", "Structured logging with correlation IDs",
            "metrics", "Prometheus metrics available at /actuator/prometheus",
            "tracing", "Distributed tracing with correlation IDs",
            "rateLimiting", "Rate limiting metrics at rate_limit.requests",
            "circuitBreaker", "Circuit breaker metrics at resilience4j.circuitbreaker.*"
        ));
        
        info.put("endpoints", Map.of(
            "prometheus", "/actuator/prometheus",
            "metrics", "/actuator/metrics",
            "health", "/actuator/health",
            "allMetrics", "/api/metrics/all"
        ));
        
        return ResponseEntity.ok(info);
    }
}

