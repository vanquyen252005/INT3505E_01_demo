package com.example.baitaptuan8.service;

import com.example.baitaptuan8.dto.AccountResponse;
import com.example.baitaptuan8.entity.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    // Map giả DB: accountId -> Account
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();

    public AccountResponse createAccount(Long id, BigDecimal initialBalance) {
        Account account = new Account(id, initialBalance);
        accounts.put(id, account);
        return new AccountResponse(account.getId(), account.getBalance());
    }

    public AccountResponse getAccount(Long id) {
        Account account = accounts.get(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + id);
        }
        return new AccountResponse(account.getId(), account.getBalance());
    }

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Account not found");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account " + fromId);
        }

        from.debit(amount);
        to.credit(amount);
    }
}
