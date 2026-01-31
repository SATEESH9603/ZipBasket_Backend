package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.CheckoutRequestDto;
import com.example.onlinetest.Domain.Dto.CheckoutResponseDto;

public interface ICheckoutService {
    CheckoutResponseDto checkout(CheckoutRequestDto request);
}
