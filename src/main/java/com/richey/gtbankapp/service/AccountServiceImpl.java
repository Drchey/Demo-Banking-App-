package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import com.richey.gtbankapp.model.Account;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.repo.AccountRepo;
import com.richey.gtbankapp.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    private String generateIban() {
        final String newIban = Iban.random(CountryCode.TN)
                .toFormattedString();

        boolean ibanAlreadyExists = accountRepo.existsByIban(newIban);
        if (ibanAlreadyExists) {
            generateIban();
        }
        return newIban;
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {
        // Check if the User in Request exists
        User user = userRepo.findById(request.user_id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found")
        );
        // Check if this Account
        if (accountRepo.existsByUserId(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already has an account"
            );
        }

        // Create New Account
        String iban = generateIban();

        Account account = Account.builder().iban(iban).user(user).build();

        Account savedAccount = accountRepo.save(account);

        return new AccountResponse(
            savedAccount.getIban(),
            savedAccount.getUser().getFirstName(),
                savedAccount.getUser().getLastName(), savedAccount.getUser().getEmail(),
        savedAccount.isLocked()
        );
    }

    @Override
    public AccountResponse toggleAccountLock(Long accountId) {
        // Check if Account exists
        Account account = accountRepo.findById(accountId).orElseThrow(()
        -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found"));
        // Toggle the Account of User
       account.setLocked(!account.isLocked());
       Account savedAccount = accountRepo.save(account);

       return new AccountResponse(
            savedAccount.getIban(),
            savedAccount.getUser().getFirstName(),
            savedAccount.getUser().getLastName(),
            savedAccount.getUser().getEmail(),
            savedAccount.isLocked()
       );

    }

    @Override
    public List<AccountResponse> getAllAccount() {
        return accountRepo.findAll().stream().map(
                account ->new AccountResponse(
                        account.getIban(),
                        account.getUser().getFirstName(),
                        account.getUser().getLastName(),
                        account.getUser().getEmail(),
                        account.isLocked()
                )
        ).toList();
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {
        // Check if Account Exists
        Account account = accountRepo.findById(accountId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found"));

        return new AccountResponse(
                account.getIban(),
                account.getUser().getFirstName(),
                account.getUser().getLastName(),
                account.getUser().getEmail(),
                account.isLocked()
        );
    }
}
