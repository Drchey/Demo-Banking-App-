package com.richey.gtbankapp.controller;

import com.richey.gtbankapp.dto.*;
import com.richey.gtbankapp.service.PasswordResetService;
import com.richey.gtbankapp.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    private final UserServiceImpl userService;
    private final PasswordResetService passwordResetService;


    // Register User
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegistrationRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    // Login User
    @PostMapping("/login")
    public  ResponseEntity<AuthTokenResponse> loginUser(@RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.LoginUser(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        passwordResetService.initiatePasswordReset(forgotPasswordRequest.email());
        return ResponseEntity.ok(Map.of(
                "message","If an account with email exists, a reset link has been sent"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        passwordResetService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(Map.of(
                "message", "Password has been successfully reset"
        ));
    }



}
