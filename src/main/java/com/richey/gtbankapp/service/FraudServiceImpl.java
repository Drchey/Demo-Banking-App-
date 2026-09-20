package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.TransactionWithFraudResponse;
import com.richey.gtbankapp.model.FraudType;
import com.richey.gtbankapp.repo.FraudRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudServiceImpl {

    private final FraudRepo fraudRepo;

    // Get All Transactions where Fraud exists

    public void findAllTransactionWithFraud(FraudType type){
//        return fraudRepo;
    }

    // Change Fraud Status

    //
}
