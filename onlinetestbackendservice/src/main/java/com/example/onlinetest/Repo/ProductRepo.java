package com.example.onlinetest.Repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {
    // JpaRepository already provides pagination via findAll(Pageable pageable)
}
