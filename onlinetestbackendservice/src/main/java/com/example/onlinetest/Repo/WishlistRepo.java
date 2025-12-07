package com.example.onlinetest.Repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepo extends JpaRepository<Wishlist, UUID> {
    Optional<Wishlist> findByUserName(String userName);
}
