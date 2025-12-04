package com.example.Service.Operation.audit;

import com.example.Service.Operation.service.MonitoringService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Enhanced Audit Logger với structured logging và correlation IDs
 * Tích hợp với MonitoringService để tracking requests
 */
@Component
public class AuditLogger implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("AUDIT_LOGGER");
    
    @Autowired
    private MonitoringService monitoringService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate correlation ID nếu chưa có
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = monitoringService.generateCorrelationId();
        } else {
            monitoringService.setCorrelationId(correlationId);
        }
        
        // Set correlation ID vào response header
        response.setHeader("X-Correlation-ID", correlationId);
        
        // Log request với structured format
        String user = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        
        // Structured logging với correlation ID
        logger.info("AUDIT_REQUEST | correlationId={} | method={} | uri={} | ip={} | user={} | userAgent={}", 
                correlationId, method, uri, ip, user, userAgent);
        
        // Record request start time
        request.setAttribute("startTime", System.currentTimeMillis());
        request.setAttribute("correlationId", correlationId);
        
        // Log to monitoring service
        monitoringService.logRequest(method, uri, ip, userAgent != null ? userAgent : "unknown");
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute("startTime");
        String correlationId = (String) request.getAttribute("correlationId");
        
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int statusCode = response.getStatus();
            
            // Structured logging với duration
            logger.info("AUDIT_RESPONSE | correlationId={} | method={} | uri={} | status={} | duration={}ms", 
                    correlationId, method, uri, statusCode, duration);
            
            // Log to monitoring service
            monitoringService.logResponse(method, uri, statusCode, duration);
            
            // Log performance nếu request chậm
            if (duration > 1000) {
                logger.warn("AUDIT_SLOW_REQUEST | correlationId={} | uri={} | duration={}ms", 
                        correlationId, uri, duration);
            }
        }
        
        // Log errors nếu có
        if (ex != null) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            monitoringService.logError(method, uri, ex.getMessage(), ex);
        }
        
        // Clear MDC
        monitoringService.clearCorrelationId();
        MDC.clear();
    }
}