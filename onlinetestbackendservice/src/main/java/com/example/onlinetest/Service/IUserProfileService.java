package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.ForgotPasswordRequestDto;
import com.example.onlinetest.Domain.Dto.ForgotPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.ResetPasswordRequestDto;
import com.example.onlinetest.Domain.Dto.ResetPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;

public interface IUserProfileService {
    public UpdateUserProfileResponseDto updateProfile(String userName, UpdateUserProfileRequestDto requestDto);
    public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto request);
    public ResetPasswordResponseDto resetPasswordWithToken(String token, ResetPasswordRequestDto request);
    public ResetPasswordResponseDto resetPassword(String authHeader, String resetToken, ResetPasswordRequestDto request);
}
