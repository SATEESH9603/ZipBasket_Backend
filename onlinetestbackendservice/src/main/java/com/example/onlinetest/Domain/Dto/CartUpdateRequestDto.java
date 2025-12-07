package com.example.onlinetest.Domain.Dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class CartUpdateRequestDto {
    @NotNull
    private List<CartUpdateItemDto> items;

    public List<CartUpdateItemDto> getItems() { return items; }
    public void setItems(List<CartUpdateItemDto> items) { this.items = items; }
}
