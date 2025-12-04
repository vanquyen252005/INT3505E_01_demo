package com.example.Service.Operation.rate;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Filter với metrics và logging chi tiết
 * Giới hạn số lượng requests từ mỗi IP address
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    private final Counter rateLimitAllowedCounter;
    private final Counter rateLimitRejectedCounter;
    
    public RateLimitFilter(MeterRegistry meterRegistry) {
        this.rateLimitAllowedCounter = Counter.builder("rate_limit.requests")
                .tag("status", "allowed")
                .description("Number of requests allowed by rate limiter")
                .register(meterRegistry);
        this.rateLimitRejectedCounter = Counter.builder("rate_limit.requests")
                .tag("status", "rejected")
                .description("Number of requests rejected by rate limiter")
                .register(meterRegistry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        Bucket bucket = bucketCache.computeIfAbsent(ip, this::newBucket);

        if (bucket.tryConsume(1)) {
            rateLimitAllowedCounter.increment();
            long remainingTokens = bucket.getAvailableTokens();
            logger.debug("[RATE_LIMIT] Request allowed | ip={} | uri={} | remainingTokens={}", 
                    ip, uri, remainingTokens);
            filterChain.doFilter(request, response);
        } else {
            rateLimitRejectedCounter.increment();
            logger.warn("[RATE_LIMIT] Request rejected | ip={} | uri={} | reason=rate_limit_exceeded", 
                    ip, uri);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }

    /**
     * Tạo bucket mới với rate limit: 10 requests per minute
     * Có thể customize theo endpoint hoặc user
     */
    private Bucket newBucket(String key) {
        Bandwidth limit = Bandwidth.simple(10, Duration.ofMinutes(1));
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();
        logger.info("[RATE_LIMIT] Created new bucket for IP: {}", key);
        return bucket;
    }
    
    /**
     * Get rate limit info cho monitoring
     */
    public Map<String, Long> getRateLimitInfo(String ip) {
        Bucket bucket = bucketCache.get(ip);
        if (bucket != null) {
            return Map.of(
                "availableTokens", bucket.getAvailableTokens(),
                "consumedTokens", bucket.getAvailableTokens()
            );
        }
        return Map.of();
    }
}