package com.example.onlinetest.Domain.Dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class AddressDto {
    private UUID id;
    @NotBlank
    private String type;
    @NotBlank
    private String line1;
    private String line2;
    @NotBlank
    private String city;
    private String state;
    private String postalCode;
    @NotBlank
    private String country;
    private boolean isDefault;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }
    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}
