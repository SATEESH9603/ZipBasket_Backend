package com.example.onlinetest.Repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserName(String userName);
}
