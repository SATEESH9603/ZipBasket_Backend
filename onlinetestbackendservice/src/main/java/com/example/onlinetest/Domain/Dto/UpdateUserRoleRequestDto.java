package com.example.onlinetest.Domain.Dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a user's role")
public class UpdateUserRoleRequestDto {

    @NotBlank
    @Schema(description = "New role to assign (ADMIN, SELLER, USER, etc)", example = "SELLER")
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
