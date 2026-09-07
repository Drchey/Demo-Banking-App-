package com.richey.gtbankapp.dto;

import java.math.BigDecimal;

public record TransactionTransferRequest(
         String description,
         BigDecimal amount,
         String sourceIban,
         String destinationIban

) {
}
