package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionTransferRequest;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;
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
    public TransactionResponse desposit(Long userId, TransactionDepositRequest request) {

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
        Long getCurrentUserId = securityUtils.getCurrentUserId();

        User user = userRepo.findById(getCurrentUserId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


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
        Long currentUserId = securityUtils.getCurrentUserId();


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
    public List<TransactionResponse> getAllTransactions(Long userId) {

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
}
