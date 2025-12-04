# Service Operation - Hệ thống Demo Monitoring, Rate Limiting & Circuit Breaker

Hệ thống demo minh họa các tính năng quan trọng trong Service Operation:
- **Monitoring**: Logs, Metrics, Tracing
- **Rate Limiting**: Giới hạn số lượng requests
- **Circuit Breaker**: Bảo vệ hệ thống khỏi cascading failures

## 🚀 Tính năng

### 1. Monitoring (Logs, Metrics, Tracing)

#### Logs
- **Structured Logging**: Logs được format theo cấu trúc với correlation IDs
- **Audit Logging**: Ghi lại tất cả requests với thông tin chi tiết
- **Performance Logging**: Track performance của các operations
- **Log Files**: Logs được lưu tại `logs/myapp.log`

#### Metrics
- **Prometheus Metrics**: Expose metrics qua `/actuator/prometheus`
- **Custom Metrics**: 
  - `app.requests.total`: Tổng số requests
  - `app.errors.total`: Tổng số errors
  - `app.request.duration`: Thời gian xử lý requests
  - `rate_limit.requests`: Rate limiting metrics (allowed/rejected)
  - `circuit_breaker.calls`: Circuit breaker metrics (success/failure)
  - `resilience4j.circuitbreaker.*`: Resilience4j circuit breaker metrics

#### Tracing
- **Correlation IDs**: Mỗi request có correlation ID để track qua các services
- **Distributed Tracing**: Tích hợp với Micrometer Tracing (Zipkin/Brave)
- **Request Tracking**: Track requests từ đầu đến cuối

### 2. Rate Limiting

- **IP-based Rate Limiting**: Giới hạn requests theo IP address
- **Limit**: 10 requests per minute per IP
- **Metrics**: Track số lượng requests được allow/reject
- **Response**: Trả về HTTP 429 (Too Many Requests) khi vượt quá limit

### 3. Circuit Breaker

- **Resilience4j Integration**: Sử dụng Resilience4j cho circuit breaker pattern
- **Configuration**:
  - Failure rate threshold: 50%
  - Sliding window size: 10 requests
  - Minimum calls: 5 requests
  - Wait duration in open state: 10 seconds
- **Fallback**: Tự động fallback khi circuit breaker mở
- **Retry**: Tự động retry với configurable attempts
- **Timeout Protection**: Bảo vệ khỏi slow responses

## 📦 Dependencies

- Spring Boot 3.4.12
- Spring Boot Actuator (monitoring endpoints)
- Micrometer + Prometheus (metrics)
- Bucket4j (rate limiting)
- Resilience4j (circuit breaker)
- Micrometer Tracing (distributed tracing)

## 🏃 Cách chạy

1. **Build project**:
```bash
./mvnw clean install
```

2. **Run application**:
```bash
./mvnw spring-boot:run
```

3. **Application sẽ chạy tại**: `http://localhost:8082`

## 📡 API Endpoints

### Basic Endpoints
- `GET /api/hello` - Hello endpoint với monitoring
- `GET /api/health` - Health check

### Demo Endpoints
- `GET /api/demo/monitoring` - Demo monitoring features
- `GET /api/demo/rate-limit-test` - Test rate limiting
- `GET /api/demo/circuit-breaker-test` - Hướng dẫn test circuit breaker
- `GET /api/demo/features` - Xem tất cả features

### Circuit Breaker Endpoints
- `GET /api/circuit-breaker/call?serviceName=payment-service` - Gọi external service với circuit breaker
- `GET /api/circuit-breaker/call-timeout?serviceName=slow-service` - Test timeout protection
- `POST /api/circuit-breaker/simulate-failure` - Enable failure simulation để test circuit breaker
- `POST /api/circuit-breaker/disable-failure` - Disable failure simulation
- `GET /api/circuit-breaker/status` - Xem status của circuit breaker

### Metrics Endpoints
- `GET /api/metrics/all` - Xem tất cả metrics
- `GET /api/metrics/{metricName}` - Xem metric cụ thể
- `GET /api/metrics/info` - Thông tin về monitoring

### Actuator Endpoints
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - List all metrics
- `GET /actuator/prometheus` - Prometheus metrics format
- `GET /actuator/circuitbreakers` - Circuit breaker states
- `GET /actuator/circuitbreakerevents` - Circuit breaker events

## 🧪 Cách test các tính năng

### Test Rate Limiting

1. Gọi endpoint nhiều lần nhanh:
```bash
for i in {1..15}; do curl http://localhost:8082/api/demo/rate-limit-test; echo ""; done
```

2. Sau 10 requests, bạn sẽ thấy HTTP 429 (Too Many Requests)

3. Xem metrics:
```bash
curl http://localhost:8082/actuator/metrics/rate_limit.requests
```

### Test Circuit Breaker

1. **Enable failure simulation**:
```bash
curl -X POST http://localhost:8082/api/circuit-breaker/simulate-failure
```

2. **Gọi service nhiều lần để trigger circuit breaker**:
```bash
for i in {1..10}; do curl http://localhost:8082/api/circuit-breaker/call?serviceName=test-service; echo ""; done
```

3. **Xem circuit breaker metrics**:
```bash
curl http://localhost:8082/actuator/metrics/resilience4j.circuitbreaker.calls
curl http://localhost:8082/actuator/circuitbreakers
```

4. **Disable failure simulation**:
```bash
curl -X POST http://localhost:8082/api/circuit-breaker/disable-failure
```

### Test Monitoring

1. **Xem logs**:
```bash
tail -f logs/myapp.log
```

2. **Xem Prometheus metrics**:
```bash
curl http://localhost:8082/actuator/prometheus | grep -E "(rate_limit|circuit_breaker|app\.)"
```

3. **Xem correlation IDs trong logs**: Mỗi request sẽ có correlation ID trong logs

## 📊 Metrics Examples

### Rate Limiting Metrics
```
rate_limit_requests_total{status="allowed"} 150.0
rate_limit_requests_total{status="rejected"} 5.0
```

### Circuit Breaker Metrics
```
resilience4j_circuitbreaker_calls_total{name="externalService",state="success"} 100.0
resilience4j_circuitbreaker_calls_total{name="externalService",state="failure"} 10.0
resilience4j_circuitbreaker_state{name="externalService",state="CLOSED"} 1.0
```

### Application Metrics
```
app_requests_total 500.0
app_errors_total{type="application"} 2.0
app_request_duration_seconds_count 500.0
```

## 🔍 Logging Examples

### Structured Logs
```
2024-01-15 10:30:45 [http-nio-8082-exec-1] INFO  AUDIT_LOGGER - AUDIT_REQUEST | correlationId=abc123 | method=GET | uri=/api/hello | ip=127.0.0.1 | user=anonymous
2024-01-15 10:30:45 [http-nio-8082-exec-1] INFO  AUDIT_LOGGER - AUDIT_RESPONSE | correlationId=abc123 | method=GET | uri=/api/hello | status=200 | duration=15ms
```

### Rate Limiting Logs
```
[RATE_LIMIT] Request allowed | ip=127.0.0.1 | uri=/api/hello | remainingTokens=9
[RATE_LIMIT] Request rejected | ip=127.0.0.1 | uri=/api/hello | reason=rate_limit_exceeded
```

### Circuit Breaker Logs
```
[CIRCUIT_BREAKER] Attempting to call external service: payment-service
[CIRCUIT_BREAKER] Call successful: payment-service
[CIRCUIT_BREAKER] Fallback method called for: payment-service due to: External service unavailable
```

## 🛠️ Configuration

### Rate Limiting
Cấu hình trong `RateLimitFilter.java`:
- Limit: 10 requests per minute
- Có thể customize theo endpoint hoặc user

### Circuit Breaker
Cấu hình trong `application.properties`:
```properties
resilience4j.circuitbreaker.instances.externalService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.externalService.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.instances.externalService.sliding-window-size=10
```

### Logging
Cấu hình trong `application.properties`:
```properties
logging.file.name=logs/myapp.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
```

## 📈 Monitoring với Prometheus & Grafana

1. **Setup Prometheus**: Cấu hình Prometheus scrape metrics từ `/actuator/prometheus`
2. **Setup Grafana**: Import dashboard để visualize metrics
3. **Key Metrics to Monitor**:
   - Request rate và error rate
   - Rate limiting rejections
   - Circuit breaker state changes
   - Response times

## 🎯 Best Practices

1. **Monitoring**:
   - Sử dụng correlation IDs để track requests
   - Log structured data để dễ query
   - Monitor key business metrics

2. **Rate Limiting**:
   - Set appropriate limits based on service capacity
   - Monitor rejection rates
   - Consider different limits for different endpoints

3. **Circuit Breaker**:
   - Tune thresholds based on service characteristics
   - Monitor circuit breaker state changes
   - Use fallback methods appropriately

## 📝 Notes

- Correlation IDs được tự động generate và include trong response headers (`X-Correlation-ID`)
- Metrics được expose qua Prometheus format tại `/actuator/prometheus`
- Circuit breaker tự động recover sau khi service khôi phục
- Rate limiting reset mỗi phút

## 🔗 Tài liệu tham khảo

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/)
- [Resilience4j](https://resilience4j.readme.io/)
- [Bucket4j](https://github.com/bucket4j/bucket4j)

