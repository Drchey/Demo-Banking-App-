package com.richey.gtbankapp.controller;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import com.richey.gtbankapp.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;


    // Get All
    @GetMapping("/")
    public ResponseEntity<List<AccountResponse>> getAllAccount(){
        return ResponseEntity.ok(accountService.getAllAccount());
    }

    @GetMapping("/user_details")
    public ResponseEntity<AccountResponse> getUserAccount(){
        return ResponseEntity.ok(accountService.getUserAccount());
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request){
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    // Toggle Account  Mapping
    @PostMapping("/{accountId}")
    public ResponseEntity<AccountResponse> toggleAccount(@PathVariable Long accountId){
        return ResponseEntity.ok(accountService.toggleAccountLock(accountId));
    }

    // Get Account By Id
    @GetMapping("/accountId")
    public ResponseEntity<AccountResponse> getAccountDetails(@PathVariable Long accountId){
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }



}
