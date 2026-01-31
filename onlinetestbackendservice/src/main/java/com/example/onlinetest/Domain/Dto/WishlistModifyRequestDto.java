package com.example.onlinetest.Domain.Dto;

import jakarta.validation.constraints.NotBlank;

public class WishlistModifyRequestDto {
    @NotBlank
    private String productId;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
}
