package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.CartUpdateRequestDto;
import com.example.onlinetest.Domain.Dto.CartUpdateResponseDto;
import com.example.onlinetest.Domain.Dto.CartViewResponseDto;

public interface ICartService {
    CartViewResponseDto viewCart(String userName);
    CartUpdateResponseDto updateCart(String userName, CartUpdateRequestDto request);
}
