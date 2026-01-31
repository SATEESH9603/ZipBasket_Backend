package com.example.onlinetest.Domain.Dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequestDto {
    @NotBlank
    private String userName;
    @NotNull
    private UUID shippingAddressId;
    @NotNull
    private UUID billingAddressId;
    @NotBlank
    private String shippingMethod; // STANDARD/EXPRESS/OVERNIGHT

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public UUID getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(UUID shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public UUID getBillingAddressId() { return billingAddressId; }
    public void setBillingAddressId(UUID billingAddressId) { this.billingAddressId = billingAddressId; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
}
