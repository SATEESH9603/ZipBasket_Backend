package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Domain.ErrorModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    // Assuming profileImage is a base64-encoded string or similar
    private String profileImage;
    private ErrorModel error;
}
