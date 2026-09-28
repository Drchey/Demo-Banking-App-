package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.ResetPasswordRequest;
import com.richey.gtbankapp.handler.InvalidResetTokenException;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final ResetTokenService resetTokenService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    public void initiatePasswordReset(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            String rawToken = resetTokenService.issueToken(email);
            String resetLink = "https://yourapp.com/reset-password?token=" + rawToken;
            mailService.sendPlainText(email, "Reset Token", resetLink);
        });
    }


    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.email().trim().toLowerCase();

        resetTokenService.validateAndConsume(email, request.resetToken());

        User user = userRepo.findByEmail(email)
                .orElseThrow(InvalidResetTokenException::new);

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());

        userRepo.save(user); // optional inside @Transactional, dirty checking would flush anyway
    }
}
