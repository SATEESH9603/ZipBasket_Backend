package com.example.onlinetest.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.CartUpdateRequestDto;
import com.example.onlinetest.Domain.Dto.CartUpdateResponseDto;
import com.example.onlinetest.Domain.Dto.CartViewResponseDto;
import com.example.onlinetest.Service.ICartService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "APIs for viewing and editing user cart")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "View cart", description = "View cart items for the given user")
    @GetMapping("/view/{userName}")
    public ResponseEntity<CartViewResponseDto> viewCart(@PathVariable String userName) {
        CartViewResponseDto response = cartService.viewCart(userName);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update cart", description = "Replace cart items for the given user")
    @PatchMapping("/update/{userName}")
    public ResponseEntity<CartUpdateResponseDto> updateCart(@PathVariable String userName, @RequestBody @jakarta.validation.Valid CartUpdateRequestDto request) {
        CartUpdateResponseDto response = cartService.updateCart(userName, request);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
