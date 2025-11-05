package com.example.onlinetest.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.UserLoginRequestDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;
import com.example.onlinetest.Service.IUserAuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final IUserAuthenticationService authenticationService;

    public AuthController(IUserAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto userDto) {
        // Here you would typically handle the login logic
        var res = authenticationService.login(userDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDto> register(@RequestBody UserRegisterRequestDto userDetails) {
        // Here you would typically handle the registration logic
        var res = authenticationService.register(userDetails);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    
}
