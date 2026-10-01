package com.richey.gtbankapp;

import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionTransferRequest;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;
import com.richey.gtbankapp.handler.InsufficentFundsException;
import com.richey.gtbankapp.handler.InvalidAmountException;
import com.richey.gtbankapp.model.Account;
import com.richey.gtbankapp.model.Transaction;
import com.richey.gtbankapp.model.TransactionStatus;
import com.richey.gtbankapp.model.TransactionType;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.repo.AccountRepo;
import com.richey.gtbankapp.repo.TransactionRepo;
import com.richey.gtbankapp.repo.UserRepo;
import com.richey.gtbankapp.security.SecurityUtils;
import com.richey.gtbankapp.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    private static final String SOURCE_IBAN = "TN5904018104004942712345";
    private static final String DEST_IBAN = "TN5904018104004942719999";

    @Mock private TransactionRepo transactionRepo;
    @Mock private AccountRepo accountRepo;
    @Mock private UserRepo userRepo;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        // Adjust to your entities (setters/builders)
        sourceAccount = Account.builder().iban(SOURCE_IBAN).build();
        destinationAccount = Account.builder().iban(DEST_IBAN).build();

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@gmail.com")
                .password("password")
                .build();
        user.setAccount(sourceAccount); // withdraw() uses user.getAccount().getIban()
    }

    private Transaction captureSavedTransaction() {
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepo).save(captor.capture());
        return captor.getValue();
    }

    // ---------------------- deposit ----------------------

    @Test
    @DisplayName("deposit: saves a COMPLETED DEPOSIT for the current user")
    void deposit_success() {
        // Adjust argument order to your record: (amount, description)
        TransactionDepositRequest request =
                new TransactionDepositRequest("Deposit",new BigDecimal("500.00"),  "COMPLETED", user, "DEPOSIT"); new TransactionDepositRequest("Deposit",new BigDecimal("500.00"),  "COMPLETED", user, "DEPOSIT"); new TransactionDepositRequest("Deposit",new BigDecimal("500.00"),  "COMPLETED", user, "DEPOSIT"); new TransactionDepositRequest("Deposit",new BigDecimal("500.00"),  "COMPLETED", user, "DEPOSIT");

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.deposit(request);

        Transaction saved = captureSavedTransaction();
        assertEquals(TransactionType.DEPOSIT, saved.getType());
        assertEquals(TransactionStatus.COMPLETED, saved.getStatus());
        assertEquals(new BigDecimal("500.00"), saved.getAmount());
        assertEquals("Deposit", saved.getDescription());
        assertSame(user, saved.getUser());

        assertEquals("Deposit", response.description());
        assertEquals(new BigDecimal("500.00"), response.amount());
        assertEquals(TransactionType.DEPOSIT, response.type());
        assertEquals(TransactionStatus.COMPLETED, response.status());
    }

    @Test
    @DisplayName("deposit: throws 404 when current user does not exist")
    void deposit_userNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.deposit(
                        new TransactionDepositRequest("Deposit",new BigDecimal("500.00"),  "COMPLETED", user, "DEPOSIT")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @Disabled("Enable after adding validatePositive(request.amount()) to deposit()")
    @DisplayName("deposit: rejects zero or negative amounts")
    void deposit_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.deposit(
                        new TransactionDepositRequest("Deposit",new BigDecimal("500.00"),  "COMPLETED", user, "DEPOSIT")));
        verify(transactionRepo, never()).save(any());
    }

    // ---------------------- withdraw ----------------------

    @Test
    @DisplayName("withdraw: saves a COMPLETED WITHDRAW when balance is sufficient")
    void withdraw_success() {
        // Adjust to your record's shape
        TransactionWithdrawRequest request = new TransactionWithdrawRequest(new BigDecimal("200.00"));

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepo.calculateBalance(SOURCE_IBAN)).thenReturn(new BigDecimal("1000.00"));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.withdraw(request);

        Transaction saved = captureSavedTransaction();
        assertEquals(TransactionType.WITHDRAW, saved.getType());
        assertEquals(TransactionStatus.COMPLETED, saved.getStatus());
        assertEquals(new BigDecimal("200.00"), saved.getAmount());
        assertSame(user, saved.getUser());

        assertEquals(new BigDecimal("200.00"), response.amount());
        assertEquals(TransactionType.WITHDRAW, response.type());
    }

    @Test
    @DisplayName("withdraw: allows withdrawing exactly the available balance")
    void withdraw_exactBalance() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepo.calculateBalance(SOURCE_IBAN)).thenReturn(new BigDecimal("200.00"));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                transactionService.withdraw(new TransactionWithdrawRequest(new BigDecimal("200.00"))));
    }

    @Test
    @DisplayName("withdraw: throws InsufficentFundsException when balance is too low")
    void withdraw_insufficientFunds() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepo.calculateBalance(SOURCE_IBAN)).thenReturn(new BigDecimal("100.00"));

        assertThrows(InsufficentFundsException.class,
                () -> transactionService.withdraw(new TransactionWithdrawRequest(new BigDecimal("200.00"))));

        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("withdraw: throws InvalidAmountException for zero amount")
    void withdraw_zeroAmount() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.withdraw(new TransactionWithdrawRequest(BigDecimal.ZERO)));

        verifyNoInteractions(userRepo, transactionRepo);
    }

    @Test
    @DisplayName("withdraw: throws InvalidAmountException for negative amount")
    void withdraw_negativeAmount() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.withdraw(new TransactionWithdrawRequest(new BigDecimal("-5"))));

        verifyNoInteractions(userRepo, transactionRepo);
    }

    @Test
    @DisplayName("withdraw: throws InvalidAmountException for null amount")
    void withdraw_nullAmount() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.withdraw(new TransactionWithdrawRequest(null)));

        verifyNoInteractions(userRepo, transactionRepo);
    }

    @Test
    @DisplayName("withdraw: throws 404 when current user does not exist")
    void withdraw_userNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.withdraw(new TransactionWithdrawRequest(new BigDecimal("50"))));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(transactionRepo, never()).save(any());
    }

    // ---------------------- transfer ----------------------

    @Test
    @DisplayName("transfer: saves a COMPLETED TRANSFER between the two accounts")
    void transfer_success() {
        TransactionTransferRequest request =
                new TransactionTransferRequest("Transfer", new BigDecimal("150.00"), SOURCE_IBAN, DEST_IBAN );

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(accountRepo.findByUserId(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findByIban(DEST_IBAN)).thenReturn(Optional.of(destinationAccount));
        when(transactionRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.transfer(request);

        Transaction saved = captureSavedTransaction();
        assertEquals(SOURCE_IBAN, saved.getSourceIban());
        assertEquals(DEST_IBAN, saved.getDestinationIban());
        assertEquals(new BigDecimal("150.00"), saved.getAmount());
        assertEquals(TransactionType.TRANSFER, saved.getType());
        assertEquals(TransactionStatus.COMPLETED, saved.getStatus());

        assertEquals(new BigDecimal("150.00"), response.amount());
        assertEquals(TransactionType.TRANSFER, response.type());
    }

    @Test
    @DisplayName("transfer: throws InvalidAmountException for zero or negative amount")
    void transfer_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> transactionService.transfer(
                        new TransactionTransferRequest("Transfer", BigDecimal.ZERO, SOURCE_IBAN, DEST_IBAN )));
        assertThrows(InvalidAmountException.class,
                () -> transactionService.transfer(
                        new TransactionTransferRequest("Transfer", new BigDecimal("-1"), SOURCE_IBAN, DEST_IBAN )));

        verifyNoInteractions(accountRepo, transactionRepo);
    }

    @Test
    @DisplayName("transfer: throws IllegalArgumentException when source and destination IBANs in the request match")
    @Disabled("generating iban")
    void transfer_sameIbanInRequest() {
        TransactionTransferRequest request =
                new TransactionTransferRequest("Transfer", new BigDecimal("50"), SOURCE_IBAN, DEST_IBAN );

        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(request));

        verifyNoInteractions(accountRepo, transactionRepo);
    }

    @Test
    @DisplayName("transfer: throws 404 when the sender has no account")
    void transfer_sourceAccountNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(accountRepo.findByUserId(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.transfer(
                        new TransactionTransferRequest("Transfer", new BigDecimal("50"), SOURCE_IBAN, DEST_IBAN )));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("transfer: throws 404 when the destination account does not exist")
    void transfer_destinationNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(accountRepo.findByUserId(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findByIban(DEST_IBAN)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.transfer(
                        new TransactionTransferRequest("Transfer", new BigDecimal("50.00"), SOURCE_IBAN, DEST_IBAN )));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @DisplayName("transfer: throws 409 when the resolved accounts are the same")
    void transfer_resolvedAccountsSame() {
        // Request IBANs differ, but the destination resolves to the sender's own account
        Account sameAsSource = Account.builder().iban(SOURCE_IBAN).build();

        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(accountRepo.findByUserId(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findByIban(DEST_IBAN)).thenReturn(Optional.of(sameAsSource));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> transactionService.transfer(
                        new TransactionTransferRequest("Transfer", new BigDecimal("150.00"), SOURCE_IBAN, DEST_IBAN )));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(transactionRepo, never()).save(any());
    }

    @Test
    @Disabled("Enable after adding a balance check to transfer()")
    @DisplayName("transfer: throws InsufficentFundsException when balance is too low")
    void transfer_insufficientFunds() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(accountRepo.findByUserId(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findByIban(DEST_IBAN)).thenReturn(Optional.of(destinationAccount));
        when(transactionRepo.calculateBalance(SOURCE_IBAN)).thenReturn(new BigDecimal("10.00"));

        assertThrows(InsufficentFundsException.class,
                () -> transactionService.transfer(
                        new TransactionTransferRequest("Transfer", new BigDecimal("150.00"), SOURCE_IBAN, DEST_IBAN )));

        verify(transactionRepo, never()).save(any());
    }

    // ---------------------- getAllTransactions ----------------------

    @Test
    @DisplayName("getAllTransactions: maps every transaction to a response")
    void getAllTransactions_success() {
        Transaction t1 = Transaction.builder()
                .description("Salary").amount(new BigDecimal("500"))
                .status(TransactionStatus.COMPLETED).type(TransactionType.DEPOSIT).build();
        Transaction t2 = Transaction.builder()
                .description("Rent").amount(new BigDecimal("200"))
                .status(TransactionStatus.COMPLETED).type(TransactionType.WITHDRAW).build();
        when(transactionRepo.findAll()).thenReturn(List.of(t1, t2));

        List<TransactionResponse> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
        assertEquals("Salary", result.get(0).description());
        assertEquals(TransactionType.DEPOSIT, result.get(0).type());
        assertEquals("Rent", result.get(1).description());
        assertEquals(TransactionType.WITHDRAW, result.get(1).type());
    }

    @Test
    @DisplayName("getAllTransactions: returns empty list when there are none")
    void getAllTransactions_empty() {
        when(transactionRepo.findAll()).thenReturn(List.of());

        assertTrue(transactionService.getAllTransactions().isEmpty());
    }

    // ---------------------- getAllUserTransaction ----------------------

    @Test
    @DisplayName("getAllUserTransaction: returns only the current user's transactions")
    void getAllUserTransaction_success() {
        Transaction t1 = Transaction.builder()
                .description("Salary").amount(new BigDecimal("500"))
                .status(TransactionStatus.COMPLETED).type(TransactionType.DEPOSIT).build();
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(transactionRepo.findAllByUserId(1L)).thenReturn(List.of(t1));

        List<TransactionResponse> result = transactionService.getAllUserTransaction();

        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).description());
        assertEquals(new BigDecimal("500"), result.get(0).amount());
    }

    @Test
    @DisplayName("getAllUserTransaction: returns empty list when the user has no transactions")
    void getAllUserTransaction_empty() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(transactionRepo.findAllByUserId(1L)).thenReturn(List.of());

        assertTrue(transactionService.getAllUserTransaction().isEmpty());
    }
}