package com.example.onlinetest.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.CreateProductRequestDto;
import com.example.onlinetest.Domain.Dto.CreateProductResponseDto;
import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateProductResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateProductRequestDto;
import com.example.onlinetest.Domain.Dto.ProductResponseDto;
import com.example.onlinetest.Repo.UserRepo;
import com.example.onlinetest.Service.IProductService;
import com.example.onlinetest.Service.JwtToken.IJwtService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Catalog", description = "APIs for managing product catalog")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService, IJwtService jwtService, UserRepo userRepo) {
        this.productService = productService;
    }
        
    @GetMapping
    public ResponseEntity<ProductsListResponseDto> listProducts(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "category", defaultValue = "") String category) {
        ProductsListResponseDto response = productService.listProducts(page, category);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new product", description = "Insert product details into catalog")
    @org.springframework.web.bind.annotation.PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateProductResponseDto> createProduct(
        @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid CreateProductRequestDto request
    ) {
        // Authorization and role checks are enforced by JwtAdminFilter before reaching this controller.
        CreateProductResponseDto response = productService.createProduct(request);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @io.swagger.v3.oas.annotations.Operation(summary = "Fetch a product", description = "Fetch product details by product ID")
    @GetMapping("/getProduct")
    public ResponseEntity<ProductResponseDto> getProductById(
        @RequestParam(required = true) String productId) {
        ProductResponseDto response = productService.getProductById( productId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update a product", description = "Update product details by product ID")
    @PatchMapping("/updateProduct")
    public ResponseEntity<UpdateProductResponseDto> UpdateProductById(
        @RequestParam(value = "productId", defaultValue = "", required = true) String productId,
        @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid UpdateProductRequestDto request) {
        UpdateProductResponseDto response = productService.updateProduct( productId, request);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
