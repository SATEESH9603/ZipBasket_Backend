package com.example.onlinetest.Repo;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "addresses")
@Getter
@Setter
public class Address extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", length = 32)
    private String type; // SHIPPING or BILLING

    @Column(name = "line1", nullable = false)
    private String line1;
    @Column(name = "line2")
    private String line2;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "state")
    private String state;
    @Column(name = "postal_code", length = 24)
    private String postalCode;
    @Column(name = "country", length = 64)
    private String country;
    @Column(name = "is_default")
    private boolean isDefault;
}
