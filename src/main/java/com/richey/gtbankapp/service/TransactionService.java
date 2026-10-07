package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionTransferRequest;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface TransactionService {

    // Deposit
    TransactionResponse deposit(String idempotencyKey, TransactionDepositRequest request);

    // Withdraw
    TransactionResponse withdraw(String idempotencyKey,TransactionWithdrawRequest request);

     TransactionResponse transfer( String idempotencyKey,TransactionTransferRequest request);

    // Get All Transactions with a User
    List<TransactionResponse> getAllTransactions();

    // Get All Transactions from User
    List<TransactionResponse> getAllUserTransaction();

    // Get All Transactions with Fraud

}
