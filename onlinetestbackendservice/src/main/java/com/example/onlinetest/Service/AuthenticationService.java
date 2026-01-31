package com.example.onlinetest.Service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.onlinetest.Domain.Dto.UserLoginRequestDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;
import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.UserRepo;
import com.example.onlinetest.Domain.Exceptions.UserAlreadyExistException;
import com.example.onlinetest.Domain.Exceptions.UserNotFoundException;
import com.example.onlinetest.Service.JwtToken.IJwtService;

@Service
public class AuthenticationService implements IUserAuthenticationService {
    private final UserRepo userRepo;
    private final IJwtService jwtService;
    // Constructor injection for UserRepo
    public AuthenticationService(UserRepo userRepo, IJwtService jwtService) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto userDetails) {
        var userOpt = userRepo.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(userDetails.getPassword())) {
            // Increment loginAttempts if user exists
            userOpt.ifPresent(u -> {
                u.setLoginAttempts(u.getLoginAttempts() + 1);
                userRepo.save(u);
            });
            throw new UserNotFoundException("User with username " + userDetails.getUsername() + " does not exist or password is incorrect");
        }
        User user = userOpt.get();
        // Reset loginAttempts and set lastLogin
        user.setLoginAttempts(0);
        user.setLastLogin(Instant.now().toString());
        userRepo.save(user);
        return Mapper.toUserLoginResponseDto(user, jwtService.generateToken(user));
    }

    @Override
    public UserRegisterResponseDto register(UserRegisterRequestDto userDetails) {
        var user = Mapper.toUser(userDetails);
        userRepo.findByUsername(user.getUsername())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistException("User with username "+userDetails.getUsername()+" already exists");
                });
        User createdUser = userRepo.save(user);
        return Mapper.toUserRegisterResponseDto(createdUser);
    }  

}
