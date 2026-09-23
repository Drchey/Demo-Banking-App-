package com.richey.gtbankapp.controller;

import com.richey.gtbankapp.dto.AccountRequest;
import com.richey.gtbankapp.dto.AccountResponse;
import com.richey.gtbankapp.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<AccountResponse>> getAllAccount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(accountService.getAllAccount(pageable));
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
