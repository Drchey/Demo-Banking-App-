package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionWithFraudResponse;
import com.richey.gtbankapp.model.*;
import com.richey.gtbankapp.repo.FraudRepo;
import com.richey.gtbankapp.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudServiceImpl {

    private final FraudRepo fraudRepo;
    private final TransactionRepo transactionRepo;

    // Get All Transactions where Fraud exists

    public List<TransactionWithFraudResponse> findAllTransactionWithFraud(FraudType type){
        return transactionRepo.findAllTransactionWithFraud(type).stream().map(
                transaction -> new TransactionWithFraudResponse(
                        transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getCreatedAt(),
                        transaction.getStatus(),
                        transaction.getType(),
                        transaction.getSourceIban(),
                        transaction.getDestinationIban(),
                        transaction.getFraud().getStatus(),
                        transaction.getFraud().getType(),
                        transaction.getFraud().getRiskLevel()
                )
        ).toList();
    }

}
