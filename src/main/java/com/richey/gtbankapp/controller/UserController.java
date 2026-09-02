package com.richey.gtbankapp.controller;

import com.richey.gtbankapp.dto.AuthTokenResponse;
import com.richey.gtbankapp.dto.LoginRequest;
import com.richey.gtbankapp.dto.RegistrationRequest;
import com.richey.gtbankapp.dto.UserResponse;
import com.richey.gtbankapp.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    private final UserServiceImpl userService;


    // Register User
    @PostMapping("")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegistrationRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    // Login User
    @PostMapping("/login")
    public  ResponseEntity<AuthTokenResponse> loginUser(@RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.LoginUser(request));
    }

}
