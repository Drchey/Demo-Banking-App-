package com.richey.gtbankapp.dto;

import com.richey.gtbankapp.model.User;

import java.math.BigDecimal;

public record TransactionDepositRequest(
        String description,
        BigDecimal amount,
        String status,
        User user,
        String type
) {
}
