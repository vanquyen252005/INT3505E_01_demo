package com.example.Service.Operation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Service mô phỏng gọi external API
 * Có thể fail hoặc chậm để demo circuit breaker
 */
@Service
public class ExternalService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalService.class);
    private final Random random = new Random();
    private int failureCount = 0;
    private boolean simulateFailure = false;
    
    /**
     * Gọi external API - có thể fail hoặc chậm
     */
    public String callExternalApi(String serviceName) throws InterruptedException {
        logger.info("[EXTERNAL_SERVICE] Calling external API: {}", serviceName);
        
        // Simulate network delay
        int delay = random.nextInt(500) + 100; // 100-600ms
        Thread.sleep(delay);
        
        // Simulate failures if enabled
        if (simulateFailure) {
            failureCount++;
            if (failureCount % 3 != 0) { // Fail 2 out of 3 times
                logger.error("[EXTERNAL_SERVICE] External API call failed for: {}", serviceName);
                throw new RuntimeException("External service unavailable: " + serviceName);
            }
        }
        
        logger.info("[EXTERNAL_SERVICE] External API call successful for: {}", serviceName);
        return "Response from " + serviceName + " (delay: " + delay + "ms)";
    }
    
    /**
     * Gọi external API với timeout
     */
    public String callExternalApiWithTimeout(String serviceName) throws InterruptedException {
        logger.info("[EXTERNAL_SERVICE] Calling external API with timeout: {}", serviceName);
        
        // Simulate slow response (could timeout)
        int delay = random.nextInt(2000) + 500; // 500-2500ms
        Thread.sleep(delay);
        
        if (delay > 1500) {
            logger.warn("[EXTERNAL_SERVICE] External API call took too long: {}ms", delay);
        }
        
        return "Response from " + serviceName + " (delay: " + delay + "ms)";
    }
    
    public void enableFailureSimulation() {
        this.simulateFailure = true;
        logger.warn("[EXTERNAL_SERVICE] Failure simulation enabled");
    }
    
    public void disableFailureSimulation() {
        this.simulateFailure = false;
        this.failureCount = 0;
        logger.info("[EXTERNAL_SERVICE] Failure simulation disabled");
    }
    
    public boolean isFailureSimulationEnabled() {
        return simulateFailure;
    }
}

