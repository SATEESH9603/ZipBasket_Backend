package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Domain.ErrorModel;
import com.example.onlinetest.Repo.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponseDto {
    public User user;
    public String token;
    public ErrorModel error;
}
