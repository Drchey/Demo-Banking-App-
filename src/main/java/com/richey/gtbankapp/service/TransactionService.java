package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;

import java.util.List;

public interface TransactionService {

    // Deposit
    TransactionResponse desposit(Long userId, TransactionDepositRequest request);

    // Withdraw
    TransactionResponse withdraw(TransactionWithdrawRequest request);

    // Get All Transactions with a User
    List<TransactionResponse> getAllTransactions(Long userId);

    // Get All Transactions from User
    List<TransactionResponse> getAllUserTransaction();

    // Get All Transactions with Fraud

}
