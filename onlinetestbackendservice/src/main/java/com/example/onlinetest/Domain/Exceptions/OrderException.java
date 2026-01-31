package com.example.onlinetest.Domain.Exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) { super(message); }
    public OrderException(String message, Throwable cause) { super(message, cause); }
}
