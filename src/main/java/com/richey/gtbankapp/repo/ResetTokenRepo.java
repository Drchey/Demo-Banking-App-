package com.richey.gtbankapp.repo;

import com.richey.gtbankapp.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepo extends JpaRepository<ResetToken, Long> {

    Optional<ResetToken> findByEmailAndUsedFalse(String email);

    @Modifying
    @Query("UPDATE ResetToken r SET r.used = true WHERE r.email = :email AND r.used = false")
    void invalidateAllForEmail(String email);
}
