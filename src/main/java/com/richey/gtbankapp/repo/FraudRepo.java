package com.richey.gtbankapp.repo;

import com.richey.gtbankapp.dto.TransactionWithFraudResponse;
import com.richey.gtbankapp.model.Fraud;
import com.richey.gtbankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudRepo extends JpaRepository<Fraud, Long> {





}
