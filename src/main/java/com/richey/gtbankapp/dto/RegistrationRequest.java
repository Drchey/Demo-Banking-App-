package com.richey.gtbankapp.dto;

public record RegistrationRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
