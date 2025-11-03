package com.example.onlinetest.Repo;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    // Reference to seller user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    public User seller;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description", columnDefinition = "VARCHAR(MAX)")
    public String description;

    @Column(name = "price", precision = 19, scale = 4, nullable = false)
    public BigDecimal price;

    @Column(name = "currency", length = 8)
    public String currency;

    @Column(name = "quantity", nullable = false)
    public int quantity = 0;

    @Column(name = "sku", unique = true)
    public String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    public Category category;

    // Store image URLs as JSON array or comma-separated values
    @Column(name = "images", columnDefinition = "VARCHAR(MAX)")
    public String images;

    @Column(name = "weight")
    public Double weight;

    @Column(name = "dimensions")
    public String dimensions;

    @Column(name = "is_active", nullable = false)
    public boolean isActive = true;

    // Any additional attributes (stored as JSON string)
    @Column(name = "metadata", columnDefinition = "VARCHAR(MAX)")
    public String metadata;

    // Timestamp fields now inherited from BaseModel

}
