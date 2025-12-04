package com.example.Service.Operation.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service quản lý monitoring: logs, metrics, và tracing
 * Cung cấp structured logging và custom metrics
 */
@Service
public class MonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE_LOGGER");
    
    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer requestTimer;
    
    public MonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = Counter.builder("app.requests.total")
                .description("Total number of requests")
                .register(meterRegistry);
        this.errorCounter = Counter.builder("app.errors.total")
                .tag("type", "application")
                .description("Total number of errors")
                .register(meterRegistry);
        this.requestTimer = Timer.builder("app.request.duration")
                .description("Request processing duration")
                .register(meterRegistry);
    }
    
    /**
     * Tạo correlation ID cho distributed tracing
     */
    public String generateCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        return correlationId;
    }
    
    /**
     * Set correlation ID vào MDC cho logging
     */
    public void setCorrelationId(String correlationId) {
        MDC.put("correlationId", correlationId);
    }
    
    /**
     * Clear correlation ID
     */
    public void clearCorrelationId() {
        MDC.remove("correlationId");
    }
    
    /**
     * Log request với structured format
     */
    public void logRequest(String method, String uri, String ip, String userAgent) {
        requestCounter.increment();
        auditLogger.info("REQUEST_INCOMING | method={} | uri={} | ip={} | userAgent={}", 
                method, uri, ip, userAgent);
    }
    
    /**
     * Log response với structured format
     */
    public void logResponse(String method, String uri, int statusCode, long duration) {
        requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        auditLogger.info("REQUEST_COMPLETED | method={} | uri={} | status={} | duration={}ms", 
                method, uri, statusCode, duration);
    }
    
    /**
     * Log error với structured format
     */
    public void logError(String method, String uri, String error, Exception exception) {
        errorCounter.increment();
        logger.error("ERROR_OCCURRED | method={} | uri={} | error={}", 
                method, uri, error, exception);
    }
    
    /**
     * Log performance metrics
     */
    public void logPerformance(String operation, long duration, String unit) {
        performanceLogger.info("PERFORMANCE | operation={} | duration={}{}", 
                operation, duration, unit);
    }
    
    /**
     * Record custom metric
     */
    public void recordCustomMetric(String metricName, String tag, double value) {
        meterRegistry.counter(metricName, "tag", tag).increment((long) value);
    }
    
    /**
     * Record business metric
     */
    public void recordBusinessMetric(String metricName, String... tags) {
        Counter.builder("business." + metricName)
                .tags(tags)
                .description("Business metric: " + metricName)
                .register(meterRegistry)
                .increment();
    }
}

