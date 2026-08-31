package com.richey.gtbankapp.dto;

public record AccountResponse(
        String iban,
        String firstName,
        String lastName,
        String email,
        boolean locked
) {
}
