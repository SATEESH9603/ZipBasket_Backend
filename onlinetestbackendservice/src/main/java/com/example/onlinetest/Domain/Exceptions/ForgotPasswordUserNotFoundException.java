package com.example.onlinetest.Domain.Exceptions;

public class ForgotPasswordUserNotFoundException extends RuntimeException {
    public ForgotPasswordUserNotFoundException(String message) {
        super(message);
    }

    public ForgotPasswordUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }   

}
