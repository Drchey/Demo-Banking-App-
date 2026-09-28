package com.richey.gtbankapp.service;

import com.richey.gtbankapp.handler.InvalidResetTokenException;
import com.richey.gtbankapp.model.ResetToken;
import com.richey.gtbankapp.repo.ResetTokenRepo;
import com.richey.gtbankapp.security.TokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetTokenService {

    private final ResetTokenRepo resetTokenRepo;
    private final TokenUtil tokenUtil;


    @Transactional
    public String issueToken(String email) {
        resetTokenRepo.invalidateAllForEmail(email);

        String rawToken = tokenUtil.generateRawToken();

        resetTokenRepo.save(ResetToken.builder()
                .email(email)
                .token(tokenUtil.hash(rawToken))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build());

        return rawToken;
    }

    @Transactional
    public void validateAndConsume(String email, String rawToken) {
        ResetToken record = resetTokenRepo.findByEmailAndUsedFalse(email)
                .orElseThrow(InvalidResetTokenException::new);

        if (record.getExpiresAt().isBefore(LocalDateTime.now())
                || !tokenUtil.matches(rawToken, record.getToken())) {
            throw new InvalidResetTokenException();
        }

        record.setUsed(true);
        resetTokenRepo.save(record);
    }
}
