package com.richey.gtbankapp.dto;

import java.math.BigDecimal;

public record TransactionWithdrawRequest(
        BigDecimal amount
) {
}
