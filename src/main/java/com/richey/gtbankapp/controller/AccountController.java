package com.richey.gtbankapp.controller;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import com.richey.gtbankapp.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request){
        return ResponseEntity.ok(accountService.createAccount(request));
    }

//    // Get Mapping
//    @GetMapping("/{id}")
//    public
}
