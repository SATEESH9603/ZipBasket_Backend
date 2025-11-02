package com.example.onlinetest.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;
import com.example.onlinetest.Service.IProductService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Catalog", description = "APIs for managing product catalog")
public class ProductController {

    private final IProductService productService;

    @Autowired
    public ProductController(IProductService productService) {
        this.productService = productService;
    }
        
    @GetMapping
    public ResponseEntity<ProductsListResponseDto> listProducts(
        @Parameter(
            description = "Page number (starts from 1)",
            example = "1"
        )
        @RequestParam(value = "page", defaultValue = "1") Integer page
    ) {
        ProductsListResponseDto response = productService.listProducts(page);
        return new ResponseEntity<>(response, 
            response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
