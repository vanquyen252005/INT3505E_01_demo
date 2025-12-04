package com.example.Service.Operation.controller;

import com.example.Service.Operation.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller demo các tính năng cơ bản với monitoring
 */
@RestController
@RequestMapping("/api")
public class HelloController {
    
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    
    @Autowired
    private MonitoringService monitoringService;
    
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        logger.info("[HELLO] Request received at /api/hello");
        monitoringService.recordBusinessMetric("hello.requests", "endpoint", "/api/hello");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello World");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        logger.info("[HEALTH] Health check requested");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Service Operation Demo");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}