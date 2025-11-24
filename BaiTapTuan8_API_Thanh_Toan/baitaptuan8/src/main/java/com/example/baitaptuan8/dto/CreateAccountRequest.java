package com.example.baitaptuan8.dto;

import java.math.BigDecimal;

public record CreateAccountRequest(
        Long accountId,
        BigDecimal initialBalance
) {}