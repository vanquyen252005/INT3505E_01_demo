package com.example.Service.Operation.config;

import com.example.Service.Operation.audit.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private AuditLogger auditLogger;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLogger);
    }
}