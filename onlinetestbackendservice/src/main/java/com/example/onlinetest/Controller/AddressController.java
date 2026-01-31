package com.example.onlinetest.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.AddressDto;
import com.example.onlinetest.Service.IAddressService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/user/address")
@Tag(name = "User Address", description = "CRUD APIs for user addresses")
public class AddressController {

    private final IAddressService addressService;

    public AddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<AddressDto>> list(@PathVariable String username) {
        List<AddressDto> response = addressService.list(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{username}")
    public ResponseEntity<AddressDto> create(@PathVariable String username, @RequestBody @jakarta.validation.Valid AddressDto request) {
        AddressDto response = addressService.create(username, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{username}/{addressId}")
    public ResponseEntity<AddressDto> update(@PathVariable String username, @PathVariable UUID addressId, @RequestBody AddressDto request) {
        AddressDto response = addressService.update(username, addressId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{username}/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable String username, @PathVariable UUID addressId) {
        boolean deleted = addressService.delete(username, addressId);
        return new ResponseEntity<>(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }
}
