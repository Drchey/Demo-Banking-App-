package com.richey.gtbankapp.controller;


import com.richey.gtbankapp.dto.TransactionDepositRequest;
import com.richey.gtbankapp.dto.TransactionResponse;
import com.richey.gtbankapp.dto.TransactionTransferRequest;
import com.richey.gtbankapp.dto.TransactionWithdrawRequest;
import com.richey.gtbankapp.service.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionServiceImpl transactionService;


    @GetMapping("")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/users")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(){
        return ResponseEntity.ok(transactionService.getAllUserTransaction());
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionDepositRequest request){
        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionWithdrawRequest request){
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransactionTransferRequest request){
        return ResponseEntity.ok(transactionService.transfer(request));
    }
}
