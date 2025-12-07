package com.example.onlinetest.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.CheckoutRequestDto;
import com.example.onlinetest.Domain.Dto.CheckoutResponseDto;
import com.example.onlinetest.Service.ICheckoutService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/checkout")
@Tag(name = "Checkout", description = "Create orders from cart")
public class CheckoutController {

    private final ICheckoutService checkoutService;

    public CheckoutController(ICheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping
    public ResponseEntity<CheckoutResponseDto> checkout(@RequestBody @jakarta.validation.Valid CheckoutRequestDto request) {
        CheckoutResponseDto resp = checkoutService.checkout(request);
        return new ResponseEntity<>(resp, resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
