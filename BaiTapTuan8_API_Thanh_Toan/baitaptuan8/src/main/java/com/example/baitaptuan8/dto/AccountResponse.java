package com.example.baitaptuan8.dto;


import java.math.BigDecimal;

public record AccountResponse(
        Long accountId,
        BigDecimal balance
) {}