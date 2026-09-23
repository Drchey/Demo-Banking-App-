package com.richey.gtbankapp.repo;

import com.richey.gtbankapp.model.FraudType;
import com.richey.gtbankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUserId(Long userId);


    @Query("""
        SELECT t from Transaction t INNER JOIN t.fraud f WHERE f.type = :type
    """)
    List<Transaction> findAllTransactionWithFraud(@Param("type") FraudType type);

}
