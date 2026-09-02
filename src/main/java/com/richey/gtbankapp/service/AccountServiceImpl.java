package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Override
    public void createAccount(AccountRequest request) {

    }

    @Override
    public AccountResponse toggleAccountLock(Long accountId) {
        return null;
    }

    @Override
    public List<AccountResponse> getAllAccount() {
        return List.of();
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {
        return null;
    }
}
