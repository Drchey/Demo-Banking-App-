package com.richey.gtbankapp.handler;


import com.richey.gtbankapp.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException exception, HttpServletRequest request){

        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Bad Credentials",
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InsufficentFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficentFunds(InsufficentFundsException exception, HttpServletRequest request){

        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Insufficient Funds",
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleArgumentException(IllegalArgumentException exception, HttpServletRequest request){

        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Cant Transfer to Same Argument",
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException exception, HttpServletRequest request) {

        HttpStatusCode statusCode = exception.getStatusCode();
        HttpStatus status = HttpStatus.resolve(statusCode.value());

        ErrorResponse body = new ErrorResponse(
                statusCode.value(),
                status != null ? status.getReasonPhrase() : "Error",
                exception.getReason(),
                request.getRequestURI(),
                LocalDateTime.now());

        return ResponseEntity.status(statusCode).body(body);
    }

}
