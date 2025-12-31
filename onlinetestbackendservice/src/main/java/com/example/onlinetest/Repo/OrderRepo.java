package com.example.onlinetest.Repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, UUID> {
    List<Order> findByUserUsername(String username);
    // Case-insensitive variants for robustness
    List<Order> findByUserUsernameIgnoreCase(String username);
    Optional<Order> findByIdAndUserUsername(UUID id, String username);
    Optional<Order> findByIdAndUserUsernameIgnoreCase(UUID id, String username);
}
