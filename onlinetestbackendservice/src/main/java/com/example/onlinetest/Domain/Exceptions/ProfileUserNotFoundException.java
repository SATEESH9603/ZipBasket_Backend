package com.example.onlinetest.Domain.Exceptions;

public class ProfileUserNotFoundException extends RuntimeException {
    public ProfileUserNotFoundException(String message) {
        super(message);
    }

    public ProfileUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }   

}
