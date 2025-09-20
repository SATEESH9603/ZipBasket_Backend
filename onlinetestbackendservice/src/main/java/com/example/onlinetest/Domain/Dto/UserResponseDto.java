package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Repo.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String id;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String createdAt;
    private String updatedAt;

    public UserResponseDto(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.createdAt = user.getCreatedAt().toString();
        this.updatedAt = user.getUpdatedAt().toString();
    }
}