package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Repo.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private String id;
    private String sellerId;
    private String name;
    private String description;
    private String price;
    private String currency;
    private int quantity;
    private String sku;
    private String category;
    private String images;
    private Double weight;
    private String dimensions;
    private boolean isActive;
    private String metadata;
    private String createdAt;
    private String updatedAt;

    public ProductDto() {}

    public ProductDto(Product p) {
        if (p.getId() != null) this.id = p.getId().toString();
        if (p.getSellerId() != null) this.sellerId = p.getSellerId().toString();
        this.name = p.getName();
        this.description = p.getDescription();
        this.price = p.getPrice() != null ? p.getPrice().toPlainString() : null;
        this.currency = p.getCurrency();
        this.quantity = p.getQuantity();
        this.sku = p.getSku();
        this.category = p.getCategory();
        this.images = p.getImages();
        this.weight = p.getWeight();
        this.dimensions = p.getDimensions();
        this.isActive = p.isActive();
        this.metadata = p.getMetadata();
        this.createdAt = p.getCreatedAt() != null ? p.getCreatedAt().toString() : null;
        this.updatedAt = p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null;
    }
}
