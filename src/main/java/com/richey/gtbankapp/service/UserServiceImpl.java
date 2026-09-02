package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.AuthTokenResponse;
import com.richey.gtbankapp.dto.LoginRequest;
import com.richey.gtbankapp.dto.RegistrationRequest;
import com.richey.gtbankapp.dto.UserResponse;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.model.UserRole;
import com.richey.gtbankapp.repo.UserRepo;
import com.richey.gtbankapp.security.JwtUtil;
import com.richey.gtbankapp.security.UserDetailsServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private  final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;


    @Override
    @Transactional
    public UserResponse createUser(RegistrationRequest request) {
        if(userRepo.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already Exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(request.password()) // Need to Encode Password
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepo.save(user);

        return new UserResponse(
            savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.isActive(),
                savedUser.getRole().name()
        );
    }

    @Override
    public AuthTokenResponse LoginUser(LoginRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        }catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepo.findByEmail(request.email()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Email or Password"));

        String token =jwtUtil.generateToken(user);
        return new AuthTokenResponse(token);
    }

    @Override
    public UserResponse findById(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not Found")
        );

        if(!user.getRole().name().equals("ROLE_ADMIN")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not Found");
        }

        return  new UserResponse(
            user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isActive(),
                user.getRole().name()
        );

    }

    @Override
    public List<UserResponse> findAllUsers() {
        return userRepo.findAll().stream().map(
                users ->new UserResponse(
                        users.getFirstName(),
                        users.getLastName(),
                        users.getEmail(),
                        users.isActive(),
                        users.getRole().name()
                )
        ).toList();
    }
}
