package com.richey.gtbankapp.handler;

public class InsufficentFundsException extends RuntimeException{
    public InsufficentFundsException(String message) {
        super(message);
    }
}
