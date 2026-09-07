package com.richey.gtbankapp.dto;

import java.math.BigDecimal;

public record TransactionDepositRequest(
        BigDecimal amount
) {
}
