package com.example.baitaptuan8.controller;

import com.example.baitaptuan8.dto.AccountResponse;
import com.example.baitaptuan8.dto.CreateAccountRequest;
import com.example.baitaptuan8.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest request) {
        AccountResponse resp = accountService.createAccount(
                request.accountId(),
                request.initialBalance()
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }
}
