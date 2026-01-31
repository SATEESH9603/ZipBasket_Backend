package com.example.onlinetest.Domain.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CartUpdateItemDto {
    @NotBlank
    private String productId;

    @Min(0)
    private int quantity;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
