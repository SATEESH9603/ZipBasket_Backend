package com.example.onlinetest.Repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {
    // Provide a pagination method that fetches the seller to avoid N+1
    @EntityGraph(attributePaths = {"seller"})
    Page<Product> findAll(Pageable pageable);
}
