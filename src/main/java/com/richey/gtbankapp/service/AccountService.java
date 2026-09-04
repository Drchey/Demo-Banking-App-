package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;

import java.util.List;

public interface AccountService {

    // Create Account
    AccountResponse createAccount(AccountRequest request);

    // Lock Account - Toggle
    AccountResponse toggleAccountLock(Long accountId);

    // get All Accounts
    List<AccountResponse> getAllAccount(); // Pagination

    // find Account By Id
    AccountResponse getAccountById(Long accountId);
}
