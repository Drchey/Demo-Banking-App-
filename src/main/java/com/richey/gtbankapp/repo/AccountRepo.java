package com.richey.gtbankapp.repo;

import com.richey.gtbankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AccountRepo extends JpaRepository<Account, Long> {

    boolean existsByIban(String iban);
    boolean existsByUserId(Long userId);
}
