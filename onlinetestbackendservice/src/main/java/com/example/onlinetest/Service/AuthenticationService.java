package com.example.onlinetest.Service;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public AuthenticationService(UserRepo userRepo, IJwtService jwtService) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto userDetails) {
        var user = userRepo.findByUsername(userDetails.getUsername());
        if (user.isEmpty() || !user.get().getPassword().equals(userDetails.getPassword())) {
            throw new UserNotFoundException("User with username " + userDetails.getUsername() + " does not exist or password is incorrect");
        }
        return Mapper.toUserLoginResponseDto(user.get(), jwtService.generateToken(user.get()));
    }

    @Override
    public UserRegisterResponseDto register(UserRegisterRequestDto userDetails) {
    
        var user = Mapper.toUser(userDetails);
        // Here you would typically handle the registration logic
        userRepo.findByUsername(user.getUsername())        
                .ifPresent(existirAlreadyExistExceptionngUser -> {
                    throw new UserAlreadyExistException("User with username "+userDetails.getUsername()+" already exists");
                });
                User createdUser = userRepo.save(user);
        return Mapper.toUserRegisterResponseDto(createdUser);
    }  

}
