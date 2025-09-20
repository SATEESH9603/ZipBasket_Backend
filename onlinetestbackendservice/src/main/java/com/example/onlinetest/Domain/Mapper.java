package com.example.onlinetest.Domain;

import com.example.onlinetest.Domain.Dto.UpdateUserProfileRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;
import com.example.onlinetest.Repo.User;

public class Mapper {
    public static UserRegisterResponseDto toUserRegisterResponseDto(User user) {
        UserRegisterResponseDto response = new UserRegisterResponseDto();
        response.user= user;
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
        response.user = user;
        response.token = token;
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
}
