package com.example.onlinetest.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.onlinetest.Domain.Dto.WishlistModifyRequestDto;
import com.example.onlinetest.Domain.Dto.WishlistModifyResponseDto;
import com.example.onlinetest.Domain.Dto.WishlistViewResponseDto;
import com.example.onlinetest.Service.IWishlistService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/user/wishlist")
@Tag(name = "Wishlist", description = "APIs for managing user wishlist")
public class WishlistController {

    private final IWishlistService wishlistService;

    public WishlistController(IWishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/view/{userName}")
    public ResponseEntity<WishlistViewResponseDto> view(@PathVariable String userName) {
        WishlistViewResponseDto resp = wishlistService.view(userName);
        return new ResponseEntity<>(resp, resp.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/add/{userName}")
    public ResponseEntity<WishlistModifyResponseDto> add(@PathVariable String userName, @RequestBody @jakarta.validation.Valid WishlistModifyRequestDto request) {
        WishlistModifyResponseDto resp = wishlistService.add(userName, request);
        return new ResponseEntity<>(resp, resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/remove/{userName}")
    public ResponseEntity<WishlistModifyResponseDto> remove(@PathVariable String userName, @RequestBody @jakarta.validation.Valid WishlistModifyRequestDto request) {
        WishlistModifyResponseDto resp = wishlistService.remove(userName, request);
        return new ResponseEntity<>(resp, resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/move-to-cart/{userName}")
    public ResponseEntity<WishlistModifyResponseDto> moveToCart(@PathVariable String userName, @RequestBody @jakarta.validation.Valid WishlistModifyRequestDto request) {
        WishlistModifyResponseDto resp = wishlistService.moveToCart(userName, request);
        return new ResponseEntity<>(resp, resp.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
