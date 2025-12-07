package com.example.onlinetest.Repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, UUID> {
    List<Order> findByUserUsername(String username);
}
