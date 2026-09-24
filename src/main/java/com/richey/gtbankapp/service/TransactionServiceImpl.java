package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionTransferRequest;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;
import com.richey.gtbankapp.handler.InsufficentFundsException;
import com.richey.gtbankapp.handler.InvalidAmountException;
import com.richey.gtbankapp.model.*;
import com.richey.gtbankapp.repo.AccountRepo;
import com.richey.gtbankapp.repo.TransactionRepo;
import com.richey.gtbankapp.repo.UserRepo;
import com.richey.gtbankapp.security.SecurityUtils;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final UserRepo userRepo;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionDepositRequest request) {

        // Get User Info
        Long getCurrentUserId = securityUtils.getCurrentUserId();

        User user = userRepo.findById(getCurrentUserId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Transaction transaction = Transaction
                        .builder()
                .description(request.description())
                .status(TransactionStatus.COMPLETED)
                .type(TransactionType.DEPOSIT)
                .user(user)
                .amount(request.amount()).build();

        Transaction savedTransaction = transactionRepo.save(transaction);

        return new TransactionResponse(
            savedTransaction.getDescription(),
            savedTransaction.getAmount(),
            savedTransaction.getCreatedAt(),
            savedTransaction.getStatus(),
                savedTransaction.getType()
        );
    }

    @Override
    public TransactionResponse withdraw(TransactionWithdrawRequest request) {

        validatePositive(request.amount());
        Long getCurrentUserId = securityUtils.getCurrentUserId();

        User user = userRepo.findById(getCurrentUserId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        BigDecimal balance = transactionRepo.calculateBalance(user.getAccount().getId());

        if(balance.compareTo(request.amount()) <0){
            throw new InsufficentFundsException("Insufficent Funds");
        }


        Transaction transaction = Transaction
                .builder()
//                .description(request.description())
                .status(TransactionStatus.COMPLETED)
                .type(TransactionType.WITHDRAW)
                .user(user)
                .amount(request.amount()).build();

        Transaction savedTransaction = transactionRepo.save(transaction);

        return new TransactionResponse(
                savedTransaction.getDescription(),
                savedTransaction.getAmount(),
                savedTransaction.getCreatedAt(),
                savedTransaction.getStatus(),
                savedTransaction.getType()
        );
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransactionTransferRequest request) {
        //get the user account
        validatePositive(request.amount());
        Long currentUserId = securityUtils.getCurrentUserId();


        if (request.sourceIban().equals(request.destinationIban())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account sourceAccount = accountRepo.findByUserId(currentUserId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );
        // get the destination account

        Account destinationAccount =accountRepo.findByIban(request.destinationIban()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
        );

        if (sourceAccount.getIban().equals(destinationAccount.getIban())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Source and destination accounts must be different");
        }

        BigDecimal amount = request.amount();

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <=0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Source and destination accounts must be different");
        }

        Transaction transaction = Transaction.builder()
                .sourceIban(sourceAccount.getIban())
                .destinationIban(destinationAccount.getIban())
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .build();

        Transaction savedTransaction =transactionRepo.save(transaction);
        return  new TransactionResponse(
                savedTransaction.getDescription(),
                savedTransaction.getAmount(),
                savedTransaction.getCreatedAt(),
                savedTransaction.getStatus(),
                savedTransaction.getType()
        );
    }


    @Override
    public List<TransactionResponse> getAllTransactions() {

        // Get All Where 
        return transactionRepo.findAll().stream().map(
                transaction -> new TransactionResponse(
                        transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getCreatedAt(),
                        transaction.getStatus(),
                        transaction.getType()
                )
        ).toList();
    }

    @Override
    public List<TransactionResponse> getAllUserTransaction() {

        Long currentUserId = securityUtils.getCurrentUserId();

        List<Transaction> transactions = transactionRepo.findAllByUserId(currentUserId);


        return transactions.stream().map(
                transaction -> new TransactionResponse(
                        transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getCreatedAt(),
                        transaction.getStatus(),
                        transaction.getType()
                )
        ).toList();
    }

    // Fraud Checks
    private boolean isFraudTransfer(BigDecimal accountBalance, BigDecimal amount){
        boolean isGreaterThan5000 = amount.compareTo(BigDecimal.valueOf(5000)) > 0;
        BigDecimal accountBalance40Percent = accountBalance.multiply(BigDecimal.valueOf(0.4));
        boolean isGreaterThan40Percent = amount.compareTo(accountBalance40Percent) > 0;

        return isGreaterThan40Percent || isGreaterThan5000;
    }


    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }
}
