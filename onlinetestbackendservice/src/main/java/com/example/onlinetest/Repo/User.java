package com.example.onlinetest.Repo;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends UserBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(name = "email", nullable = false)
    public String email;

    
    @Column(name = "username", nullable = false, unique = true)
    public String username;

    @Column(name = "password", nullable = false)
    public String password;

    @Column(name = "firstname", nullable = false)
    public String firstName;

    @Column(name = "lastname", nullable = false)
    public String lastName;

    @Column(name = "is_active", nullable = false)
    public boolean isActive = true;

    @Column(name = "role", nullable = false)
    public String role = UserRole.USER.name();

    @Column(name = "reset_token")
    private String resetToken;
    
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "bio")
    public String bio;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "address")
    public String address;

    @Column(name = "date_of_birth")
    public String dateOfBirth;

    @Column(name = "last_login")
    public String lastLogin;

    @Column(name = "login_attempts")
    public int loginAttempts = 0;

    @Column(name = "profile_image", columnDefinition = "VARCHAR(MAX)")
    public String profileImage;

}
// Remove the enum from this file. It will be placed in a separate file named userRole.java.
