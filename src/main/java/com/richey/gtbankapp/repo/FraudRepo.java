package com.richey.gtbankapp.repo;

import com.richey.gtbankapp.model.Fraud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudRepo extends JpaRepository<Fraud, Long> {

}
