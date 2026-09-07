package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;

import java.util.List;

public class TransactionServiceImpl implements TransactionService{
    @Override
    @Tra
    public TransactionResponse desposit(Long userId, TransactionDepositRequest request) {
        return null;
    }

    @Override
    public TransactionResponse withdraw(TransactionWithdrawRequest request) {
        return null;
    }

    @Override
    public List<TransactionResponse> getAllTransactions(Long userId) {
        return List.of();
    }

    @Override
    public List<TransactionResponse> getAllUserTransaction() {
        return List.of();
    }
}
