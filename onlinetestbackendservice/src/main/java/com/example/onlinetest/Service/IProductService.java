package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.CreateProductRequestDto;
import com.example.onlinetest.Domain.Dto.CreateProductResponseDto;
import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;

public interface IProductService {
    ProductsListResponseDto listProducts(Integer page);
    CreateProductResponseDto createProduct(CreateProductRequestDto request);
}