package com.example.onlinetest.Domain;

import com.example.onlinetest.Domain.Dto.UpdateUserProfileRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Domain.Dto.ProductDto;
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
}
