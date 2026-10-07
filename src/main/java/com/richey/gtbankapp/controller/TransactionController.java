package com.richey.gtbankapp.controller;


import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionTransferRequest;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;
import com.richey.gtbankapp.service.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionServiceImpl transactionService;


    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(){
        return ResponseEntity.ok(transactionService.getAllUserTransaction());
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit( @RequestHeader("Idempotency-Key") String idempotencyKey,@RequestBody TransactionDepositRequest request){
        return ResponseEntity.ok(transactionService.deposit(idempotencyKey, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestHeader("Idempotency-Key") String idempotencyKey,@RequestBody TransactionWithdrawRequest request){
        return ResponseEntity.ok(transactionService.withdraw(idempotencyKey, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestHeader("Idempotency-Key") String idempotencyKey,@RequestBody TransactionTransferRequest request){
        return ResponseEntity.ok(transactionService.transfer(idempotencyKey, request));
    }
}
