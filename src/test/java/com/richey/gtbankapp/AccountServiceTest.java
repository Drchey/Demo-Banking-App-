package com.richey.gtbankapp;


import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import com.richey.gtbankapp.model.Account;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.model.UserRole;
import com.richey.gtbankapp.repo.AccountRepo;
import com.richey.gtbankapp.repo.UserRepo;
import com.richey.gtbankapp.security.SecurityUtils;
import com.richey.gtbankapp.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private AccountRepo accountRepo;

    @Mock
    private  SecurityUtils securityUtils;

    @InjectMocks
    private AccountServiceImpl accountService;


    private User user;
    private Account account;



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


    @Test
    @DisplayName("createAccount")
    void createAccount_success(){
        AccountRequest request = new AccountRequest("TN5904018104004942712345", 1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.existsByUserId(1L)).thenReturn(false);
        when(accountRepo.existsByIban(anyString())).thenReturn(false);
        when(accountRepo.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));


        AccountResponse response = accountService.createAccount(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo).save(captor.capture());
        Account saved = captor.getValue();

        assertSame(user, saved.getUser());
        assertNotNull(saved.getIban());
        assertTrue(saved.getIban().startsWith("TN"));

        assertEquals(saved.getIban(), response.iban());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("johndoe@gmail.com", response.email());
        assertFalse(response.locked());
    }
}
