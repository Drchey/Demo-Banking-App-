package com.richey.gtbankapp;

import com.richey.gtbankapp.model.Account;
import com.richey.gtbankapp.model.Transaction;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.model.UserRole;
import com.richey.gtbankapp.repo.AccountRepo;
import com.richey.gtbankapp.repo.TransactionRepo;
import com.richey.gtbankapp.repo.UserRepo;
import com.richey.gtbankapp.security.SecurityUtils;
import com.richey.gtbankapp.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private  TransactionRepo transactionRepo;
    @Mock
    private  AccountRepo accountRepo;
    @Mock
    private  UserRepo userRepo;

    @Mock
    private  SecurityUtils securityUtils;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Account account;
    private Transaction transaction;


    @BeforeEach
    void setUp(){
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .password("password")
                .role(UserRole.ROLE_USER)
                .build();

        account = Account.builder()
                .iban("TN5904018104004942712345")
                .user(user)
                .build();

    }


    // Transaction Test Cases :
    /**
     *Deposit, Deposit to Non User
     *  WithDrawal, Withdraw Past Balance,
     * Transfer, Transfer to same account,Transfer to account not found
     */
}
