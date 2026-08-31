package com.richey.gtbankapp.dto;

public record LoginRequest(
        String email,
        String password
) {
}
