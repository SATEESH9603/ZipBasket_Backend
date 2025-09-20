package com.example.onlinetest.Service;

import com.example.onlinetest.Domain.Dto.UserLoginRequestDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;

public interface IUserAuthenticationService {
    
    UserLoginResponseDto login(UserLoginRequestDto userDetails);

    UserRegisterResponseDto register(UserRegisterRequestDto userDetails);

}
