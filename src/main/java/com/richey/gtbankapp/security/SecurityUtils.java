package com.richey.gtbankapp.security;

import com.richey.gtbankapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final SecurityConfig securityConfig;

    public User getCurrrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User user)){
                 throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized");
        }

        return  user;
    }

    public Long getCurrentUserId(){
        return getCurrrentUser().getId();
    }
}
