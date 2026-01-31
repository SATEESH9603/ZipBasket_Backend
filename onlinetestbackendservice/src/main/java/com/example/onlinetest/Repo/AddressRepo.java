package com.example.onlinetest.Repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo extends JpaRepository<Address, UUID> {
    List<Address> findByUserUsername(String username);
}
