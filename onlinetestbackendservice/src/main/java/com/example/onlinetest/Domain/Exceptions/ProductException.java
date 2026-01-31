package com.example.onlinetest.Domain.Exceptions;

public class ProductException extends RuntimeException {
    public ProductException(String message) {
        super(message);
    }

    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }
}