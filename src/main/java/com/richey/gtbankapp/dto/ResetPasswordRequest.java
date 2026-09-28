package com.richey.gtbankapp.dto;

public record ResetPasswordRequest(
        String email,
        String resetToken,
        String newPassword
) {
}
