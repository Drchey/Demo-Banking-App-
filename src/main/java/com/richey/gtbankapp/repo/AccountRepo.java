package com.richey.gtbankapp.repo;

import com.richey.gtbankapp.dto.AccountResponse;
import com.richey.gtbankapp.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface AccountRepo extends JpaRepository<Account, Long> {

    boolean existsByIban(String iban);
    boolean existsByUserId(Long userId);
    Optional<Account> findByUserId(Long userId);
    Optional<Account> findByIban(String iban);

//    Page<Account> getAllAccount(Pageable pageable);
}
