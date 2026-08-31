package com.richey.gtbankapp.dto;

public record AccountRequest(
        String iban,
        Long user_id
) {
}
