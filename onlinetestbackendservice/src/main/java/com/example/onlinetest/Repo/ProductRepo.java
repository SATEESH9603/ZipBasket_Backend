package com.example.onlinetest.Repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;         // <-- use Spring's NonNull
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {

    @Override
    @EntityGraph(attributePaths = {"seller"})
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

   @EntityGraph(attributePaths = {"seller"})
    @NonNull
    Page<Product> findByCategory(@NonNull Category category, @NonNull Pageable pageable);
    }