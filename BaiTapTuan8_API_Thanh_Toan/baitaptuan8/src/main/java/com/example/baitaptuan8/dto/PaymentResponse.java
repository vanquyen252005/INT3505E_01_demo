package com.example.baitaptuan8.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String transactionId,
        String status,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String description,
        String version
) {}