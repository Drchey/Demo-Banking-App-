package com.richey.gtbankapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class GtBankAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GtBankAppApplication.class, args);
    }

}
