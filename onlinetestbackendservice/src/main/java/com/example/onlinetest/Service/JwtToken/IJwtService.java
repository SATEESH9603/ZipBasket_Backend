package com.example.onlinetest.Service.JwtToken;

import com.example.onlinetest.Repo.User;

public interface IJwtService {
    String generateToken(User userDetails);
    boolean isTokenValid(String token, User userDetails);
    String extractUsername(String token);
}
