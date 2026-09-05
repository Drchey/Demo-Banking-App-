package com.richey.gtbankapp.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String iban,
        String firstName,
        String lastName,
        String email,
        boolean locked
) {
}
