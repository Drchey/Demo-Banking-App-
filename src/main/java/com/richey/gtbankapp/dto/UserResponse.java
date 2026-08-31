package com.richey.gtbankapp.dto;

public record UserResponse(
        String firstName,
        String lastName,
        String email,
        boolean isActive,
        String role
) {
}
