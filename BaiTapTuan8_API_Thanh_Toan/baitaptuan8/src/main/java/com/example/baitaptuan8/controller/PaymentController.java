package com.example.baitaptuan8.controller;

import com.example.baitaptuan8.dto.PaymentRequestV1;
import com.example.baitaptuan8.dto.PaymentRequestV2;
import com.example.baitaptuan8.dto.PaymentResponse;
import com.example.baitaptuan8.service.PaymentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ===== v1 – DEPRECATED =====
    @PostMapping(value = "/api/payments", headers = "X-API-Version=1")
    @Deprecated
    public ResponseEntity<PaymentResponse> transferV1(@RequestBody PaymentRequestV1 request) {

        PaymentResponse resp = paymentService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                "Transfer by v1",
                "v1"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Version", "1");
        headers.add("X-API-Deprecated", "true");
        headers.add("X-API-Deprecation-Info",
                "Payment API v1 will be removed on 2026-01-01. Please use X-API-Version=2");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resp);
    }
    // ===== v2 – API hoàn thiện hơn =====
    @PostMapping(value = "/api/payments", headers = "X-API-Version=2")
    public ResponseEntity<PaymentResponse> transferV2(@RequestBody PaymentRequestV2 request) {

        PaymentResponse resp = paymentService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                request.description(),
                "v2"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Version", "2");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resp);
    }
}

