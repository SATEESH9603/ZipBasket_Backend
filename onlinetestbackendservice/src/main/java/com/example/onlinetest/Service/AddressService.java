package com.example.onlinetest.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinetest.Domain.Dto.AddressDto;
import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Repo.Address;
import com.example.onlinetest.Repo.AddressRepo;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.UserRepo;

@Service
public class AddressService implements IAddressService {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;

    public AddressService(AddressRepo addressRepo, UserRepo userRepo) {
        this.addressRepo = addressRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<AddressDto> list(String username) {
        return Mapper.toAddressDtoList(addressRepo.findByUserUsername(username));
    }

    @Override
    @Transactional
    public AddressDto create(String username, AddressDto request) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = new Address();
        address.setUser(user);
        address.setType(request.getType());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());
        Address saved = addressRepo.save(address);
        return Mapper.toAddressDto(saved);
    }

    @Override
    @Transactional
    public AddressDto update(String username, UUID addressId, AddressDto request) {
        Address address = addressRepo.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));
        if (address.getUser() == null || address.getUser().getUsername() == null || !address.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden: address does not belong to user");
        }
        if (request.getType() != null) address.setType(request.getType());
        if (request.getLine1() != null) address.setLine1(request.getLine1());
        if (request.getLine2() != null) address.setLine2(request.getLine2());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getState() != null) address.setState(request.getState());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());
        Address saved = addressRepo.save(address);
        return Mapper.toAddressDto(saved);
    }

    @Override
    @Transactional
    public boolean delete(String username, UUID addressId) {
        Address address = addressRepo.findById(addressId).orElse(null);
        if (address == null) return false;
        if (address.getUser() == null || address.getUser().getUsername() == null || !address.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden: address does not belong to user");
        }
        addressRepo.delete(address);
        return true;
    }
}
