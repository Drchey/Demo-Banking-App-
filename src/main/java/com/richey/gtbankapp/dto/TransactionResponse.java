package com.richey.gtbankapp.dto;

import com.richey.gtbankapp.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long description,
        BigDecimal amount,
        LocalDateTime createdAt,
        String status,
        String type,
        String destinationIban
) {
}
