package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.CreateProductRequestDto;
import com.example.onlinetest.Domain.Dto.CreateProductResponseDto;
import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;
import com.example.onlinetest.Domain.Dto.ProductResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateProductRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateProductResponseDto;

public interface IProductService {
    ProductsListResponseDto listProducts(Integer page, String category, String filter);
    CreateProductResponseDto createProduct(CreateProductRequestDto request);
    ProductResponseDto getProductById(String productId);
    UpdateProductResponseDto updateProduct(String productId, UpdateProductRequestDto request);
}