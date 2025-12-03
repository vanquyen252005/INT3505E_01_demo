package com.example.Service.Operation.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditLogger implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("AUDIT_LOGGER");


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String user = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
        logger.info("[AUDIT] User={} IP={} accessed URI={} Method={}", user, request.getRemoteAddr(), request.getRequestURI(), request.getMethod());
        return true;
    }
}