package com.example.onlinetest.Repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    // You can add custom query methods here if needed
        Optional<User> findByUsername(String username);
        Optional<User> findByResetToken(String token);
}