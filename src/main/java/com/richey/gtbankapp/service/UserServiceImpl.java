package com.richey.gtbankapp.service;

import com.richey.gtbankapp.dto.RegistrationRequest;
import com.richey.gtbankapp.dto.UserResponse;
import com.richey.gtbankapp.model.User;
import com.richey.gtbankapp.model.UserRole;
import com.richey.gtbankapp.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;


    @Override
    @Transactional
    public void createUser(RegistrationRequest request) {
        if(userRepo.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already Exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .hashed_password(request.password()) // Need to Encode Password
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepo.save(user);

        return;
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
