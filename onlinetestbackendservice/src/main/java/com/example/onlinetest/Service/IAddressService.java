package com.example.onlinetest.Service;

import java.util.List;
import java.util.UUID;

import com.example.onlinetest.Domain.Dto.AddressDto;

public interface IAddressService {
    List<AddressDto> list(String username);
    AddressDto create(String username, AddressDto request);
    AddressDto update(String username, UUID addressId, AddressDto request);
    boolean delete(String username, UUID addressId);
}
