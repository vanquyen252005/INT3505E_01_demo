package com.example.baitaptuan8.dto;

import java.math.BigDecimal;

public record PaymentRequestV2(
        BigDecimal amount,
        Long fromAccountId,
        Long toAccountId,
        String description
) {}
