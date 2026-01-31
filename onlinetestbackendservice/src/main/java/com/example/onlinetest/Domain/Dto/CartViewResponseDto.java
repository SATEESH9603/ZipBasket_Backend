package com.example.onlinetest.Domain.Dto;

import java.util.ArrayList;
import java.util.List;

public class CartViewResponseDto {
    private boolean success;
    private String message;
    private List<CartItemDto> items = new ArrayList<>();

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
}
