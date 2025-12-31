package com.example.onlinetest.Repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {

    @Override
    @EntityGraph(attributePaths = {"seller"})
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"seller"})
    @NonNull
    Page<Product> findByCategory(@NonNull Category category, @NonNull Pageable pageable);

    // Pessimistic lock with eager seller fetch to avoid lazy-init issues in DTO mapping
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"seller"})
    @NonNull
    Optional<Product> findById(@NonNull UUID id);
}