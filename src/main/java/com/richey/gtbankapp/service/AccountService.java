package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;

public interface AccountService {

    // Create Account
    AccountResponse createAccount(AccountRequest request);

    // Lock Account - Toggle
    AccountResponse toggleAccountLock(Long accountId);


    // get All Accounts
    Page<AccountResponse> getAllAccount(Pageable pageable); // Pagination

    // find Account By Id
    AccountResponse getAccountById(Long accountId);


    // Get current user account details
    AccountResponse getUserAccount();
}
