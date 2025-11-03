package com.example.onlinetest.Domain.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequestDto {
    private String sellerId;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotBlank(message = "Price is required")
    private String price;

    private String currency;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private String sku;
    private String category;
    private String images;
    private Double weight;
    private String dimensions;
    private Boolean isActive;
    private String metadata;
}
