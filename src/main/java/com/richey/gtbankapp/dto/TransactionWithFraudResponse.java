package com.richey.gtbankapp.dto;

import com.richey.gtbankapp.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionWithFraudResponse(
        String description,
        BigDecimal amount,
        LocalDateTime createdAt,
        TransactionStatus status,
        TransactionType type,
        String accountIban,
        String sourceIban,
        FraudStatus fraudStatus,
        FraudType fraudType,
        FraudCheck riskLevel

) {
}
