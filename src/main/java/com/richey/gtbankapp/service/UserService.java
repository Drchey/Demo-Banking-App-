package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.AuthTokenResponse;
import com.richey.gtbankapp.dto.LoginRequest;
import com.richey.gtbankapp.dto.RegistrationRequest;
import com.richey.gtbankapp.dto.UserResponse;

import java.util.List;

public interface UserService{

    UserResponse createUser(RegistrationRequest request);

    AuthTokenResponse LoginUser(LoginRequest request);

    //void updateUser(Long userId, )

     // findById

    UserResponse findById(Long userId);

    // Get All Users
    List<UserResponse> findAllUsers();

    // Change Password
}
