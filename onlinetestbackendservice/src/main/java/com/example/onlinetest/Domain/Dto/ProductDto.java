package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.User;

import lombok.Data;


@Data
public class ProductDto {
    private String id;
    private String sellerId;
    private String sellerName;
    private String sellerContact;
    private String sellerAddress;
    private String name;
    private String description;
    private String price;
    private String currency;
    // Do not expose stock publicly; keep legacy quantity only if some clients use it
    private int quantity; // legacy field only; frontend does not render it
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
        User s = p.getSeller();
        if (s != null) {
            if (s.getId() != null) this.sellerId = s.getId().toString();
            this.sellerName = buildSellerName(s);
            this.sellerContact = s.getPhoneNumber();
            this.sellerAddress = s.getAddress();
        }
        this.name = p.getName();
        this.description = p.getDescription();
        this.price = p.getPrice() != null ? p.getPrice().toPlainString() : null;
        this.currency = p.getCurrency();
        this.quantity = p.getQuantity();
        this.sku = p.getSku();
        this.category = p.getCategory() != null ? p.getCategory().name() : null;
        this.images = p.getImages();
        this.weight = p.getWeight();
        this.dimensions = p.getDimensions();
        this.isActive = p.isActive();
        this.metadata = p.getMetadata();
        this.createdAt = p.getCreatedAt() != null ? p.getCreatedAt().toString() : null;
        this.updatedAt = p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null;
    }

    private String buildSellerName(User s) {
        String fn = s.getFirstName();
        String ln = s.getLastName();
        if (fn != null && ln != null) return (fn + " " + ln).trim();
        if (fn != null) return fn;
        if (ln != null) return ln;
        return s.getUsername();
    }
}
