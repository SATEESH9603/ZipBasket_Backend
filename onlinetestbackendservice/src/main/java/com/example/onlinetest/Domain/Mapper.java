package com.example.onlinetest.Domain;

import com.example.onlinetest.Domain.Dto.UpdateUserProfileRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;
import com.example.onlinetest.Domain.Exceptions.ProductException;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.Category;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Domain.Dto.ProductDto;
import com.example.onlinetest.Domain.Dto.UpdateProductRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateProductResponseDto;
import com.example.onlinetest.Domain.Dto.CreateProductRequestDto;
import com.example.onlinetest.Domain.Dto.ProductResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper {
    public static UserRegisterResponseDto toUserRegisterResponseDto(User user) {
        UserRegisterResponseDto response = new UserRegisterResponseDto();
        response.setUser(user);
        // Add other fields as necessary
        return response;
    }

    public static User toUser(UserRegisterRequestDto request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());        

        // Add other fields as necessary
        return user;
    }  
    public static UserLoginResponseDto toUserLoginResponseDto(User user, String token) {
        UserLoginResponseDto response = new UserLoginResponseDto();
        response.setUser(user);
        response.setToken(token);
        // Add other fields as necessary
        return response;
    } 
    
    public static User toUser(User user, UpdateUserProfileRequestDto request) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());        
        if(request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            user.setProfileImage(request.getProfileImage());
        }
        return user;
    } 

    public static UpdateUserProfileResponseDto toUpdateUserProfileResponseDto(User savedUserResponse) {
        UpdateUserProfileResponseDto response = new UpdateUserProfileResponseDto();
        response.setUsername(savedUserResponse.getUsername());
        response.setProfileImage(savedUserResponse.getProfileImage());
        response.setEmail(savedUserResponse.getEmail());
        response.setFirstName(savedUserResponse.getFirstName());
        response.setLastName(savedUserResponse.getLastName());
        return response;
    }

    // Stream-based mapping for products
    public static List<ProductDto> toProductDtoList(List<Product> products) {
        if (products == null) return java.util.Collections.emptyList();
        return products.stream()
                       .map(ProductDto::new)
                       .collect(Collectors.toList());
    }

    public static ProductDto toProductDto(Product product) {
        if (product == null) return null;
        return new ProductDto(product);
    }

    public static Product toProduct(CreateProductRequestDto request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(new BigDecimal(request.getPrice()));
        product.setCurrency(request.getCurrency());
        product.setQuantity(request.getQuantity());
        product.setSku(request.getSku());
        try {
            product.setCategory(Category.valueOf(request.getCategory().toUpperCase().replace(' ', '_')));
        } catch (IllegalArgumentException iae) {
            throw new ProductException("Invalid category: " + request.getCategory(), iae);
        }
        product.setImages(request.getImages());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setActive(request.getIsActive());
        product.setMetadata(request.getMetadata());
        return product;
    }

    public static ProductResponseDto toProductResponseDto(Product product) {
        ProductResponseDto response = new ProductResponseDto();
        response.setProduct(new ProductDto(product));
        response.setSuccess(true);
        response.setMessage("Product retrieved successfully");
        return response;
    }

    public static Product toProduct(UpdateProductRequestDto request)
     {
        Product product = new Product();
        // Update fields if present in request
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null && !request.getPrice().isBlank()) {
            product.setPrice(new BigDecimal(request.getPrice()));
        }
        if (request.getCurrency() != null) product.setCurrency(request.getCurrency());
        if (request.getQuantity() != null) product.setQuantity(request.getQuantity());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                product.setCategory(Category.valueOf(request.getCategory().toUpperCase().replace(' ', '_')));
            } catch (IllegalArgumentException iae) {
                throw new ProductException("Invalid category: " + request.getCategory(), iae);
            }
        }
        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getWeight() != null) product.setWeight(request.getWeight());
        if (request.getDimensions() != null) product.setDimensions(request.getDimensions());
        if (request.getIsActive() != null) product.setActive(request.getIsActive());
        if (request.getMetadata() != null) product.setMetadata(request.getMetadata());
        return product;
    }

    public static Product toProduct(Product existingProduct, UpdateProductRequestDto request) {
        // Update fields if present in request
        if (request.getName() != null) existingProduct.setName(request.getName());
        if (request.getDescription() != null) existingProduct.setDescription(request.getDescription());
        if (request.getPrice() != null && !request.getPrice().isBlank()) {
            existingProduct.setPrice(new BigDecimal(request.getPrice()));
        }
        if (request.getCurrency() != null) existingProduct.setCurrency(request.getCurrency());
        if (request.getQuantity() != null) existingProduct.setQuantity(request.getQuantity());
        if (request.getSku() != null) existingProduct.setSku(request.getSku());
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                existingProduct.setCategory(Category.valueOf(request.getCategory().toUpperCase().replace(' ', '_')));
            } catch (IllegalArgumentException iae) {
                throw new ProductException("Invalid category: " + request.getCategory(), iae);
            }
        }
        if (request.getImages() != null) existingProduct.setImages(request.getImages());
        if (request.getWeight() != null) existingProduct.setWeight(request.getWeight());
        if (request.getDimensions() != null) existingProduct.setDimensions(request.getDimensions());
        if (request.getIsActive() != null) existingProduct.setActive(request.getIsActive());
        if (request.getMetadata() != null) existingProduct.setMetadata(request.getMetadata());
        return existingProduct;
    }

    public static UpdateProductResponseDto toUpdateProductResponseDto(Product updatedProduct) {
        UpdateProductResponseDto response = new UpdateProductResponseDto();
        response.setProduct(new ProductDto(updatedProduct));
        response.setSuccess(true);
        response.setMessage("Product updated successfully");
        return response;
    }

}
