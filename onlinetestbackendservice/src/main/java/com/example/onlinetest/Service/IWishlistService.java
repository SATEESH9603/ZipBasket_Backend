package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.WishlistModifyRequestDto;
import com.example.onlinetest.Domain.Dto.WishlistModifyResponseDto;
import com.example.onlinetest.Domain.Dto.WishlistViewResponseDto;

public interface IWishlistService {
    WishlistViewResponseDto view(String userName);
    WishlistModifyResponseDto add(String userName, WishlistModifyRequestDto request);
    WishlistModifyResponseDto remove(String userName, WishlistModifyRequestDto request);
    WishlistModifyResponseDto moveToCart(String userName, WishlistModifyRequestDto request);
}
