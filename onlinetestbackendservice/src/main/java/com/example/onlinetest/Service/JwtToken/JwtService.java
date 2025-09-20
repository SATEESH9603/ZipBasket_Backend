package com.example.onlinetest.Service.JwtToken;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onlinetest.Configuration.JwtConfig;
import com.example.onlinetest.Repo.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService implements IJwtService {

    private final JwtConfig jwtConfig;

    @Autowired
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public String generateToken(User userDetails) {
        // Implementation for generating JWT token

        var claims = new HashMap<String, Object>();
        claims.put("role", userDetails.getRole());

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
            .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
            .compact();
    }

    @Override
    public boolean isTokenValid(String token, User userDetails) {
        // Implementation for validating JWT token
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } 
        catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("JWT token is expired", e);
            //return false;
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        } 
    }

    @Override
    public String extractUsername(String token) {
        // Implementation for extracting username from JWT token
        return Jwts.parser()
            .setSigningKey(jwtConfig.getSecret())
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    private boolean isTokenExpired(String token) {
        // Implementation for checking if the JWT token is expired
        Date expirationDate = Jwts.parser()
            .setSigningKey(jwtConfig.getSecret())
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
        return expirationDate.before(new Date());
       
    }

}
