package com.example.baitaptuan8.service;

import com.example.baitaptuan8.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {
    private final AccountService accountService;

    public PaymentService(AccountService accountService) {
        this.accountService = accountService;
    }

    // chuyển khoản thực sự: trừ tiền from, cộng tiền to
    public PaymentResponse transfer(Long fromId, Long toId, BigDecimal amount,
                                    String description, String apiVersion) {

        accountService.transfer(fromId, toId, amount);

        return new PaymentResponse(
                UUID.randomUUID().toString(),
                "SUCCESS",
                fromId,
                toId,
                amount,
                description,
                apiVersion
        );
    }
}
