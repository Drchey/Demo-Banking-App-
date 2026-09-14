package com.richey.gtbankapp.dto;

import com.richey.gtbankapp.model.TransactionStatus;
import com.richey.gtbankapp.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String description,
        BigDecimal amount,
        LocalDateTime createdAt,
        TransactionStatus status,
        TransactionType type
//        String destinationIban
) {
}
