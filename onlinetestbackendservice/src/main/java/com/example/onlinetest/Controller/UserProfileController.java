package com.example.onlinetest.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.ForgotPasswordRequestDto;
import com.example.onlinetest.Domain.Dto.ForgotPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.ResetPasswordRequestDto;
import com.example.onlinetest.Domain.Dto.ResetPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Service.IUserProfileService;

import jakarta.validation.Valid;

@Valid
@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {
    private final IUserProfileService userProfileService;
    
    public UserProfileController(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PatchMapping(value ="/update/{userName}")
    public ResponseEntity<UpdateUserProfileResponseDto> updateProfile(@Valid @RequestBody UpdateUserProfileRequestDto updateUserProfileRequest, @PathVariable String userName) {
        UpdateUserProfileResponseDto res = userProfileService.updateProfile(userName, updateUserProfileRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDto> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestDto request) {
        return new ResponseEntity<>(userProfileService.forgotPassword(request), HttpStatus.OK);
    }

     @PatchMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDto> resetPassword(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestParam(value = "token", required = false) String token,
        @RequestBody @Valid ResetPasswordRequestDto request) {
        return new ResponseEntity<>(userProfileService.resetPassword(authHeader, token, request), HttpStatus.OK);
    }
}