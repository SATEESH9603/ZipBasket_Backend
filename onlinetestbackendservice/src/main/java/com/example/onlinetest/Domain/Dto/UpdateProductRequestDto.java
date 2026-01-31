package com.example.onlinetest.Domain.Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for updating a product")
public class UpdateProductRequestDto {
    private String sellerId;
    private String name;
    private String description;
    private String price;
    private String currency;
    private Integer quantity;
    private String sku;
    private String category;
    private String images;
    private Double weight;
    private String dimensions;
    private Boolean isActive;
    private String metadata;
}
