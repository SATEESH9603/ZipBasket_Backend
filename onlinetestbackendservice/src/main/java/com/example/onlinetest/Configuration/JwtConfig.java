package com.example.onlinetest.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.example.onlinetest.Service.JwtToken.SecretKeyGenerator;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    private String secret;
    private long expiration;

    @PostConstruct
    public void initsecret() {
        if (this.secret == null || this.secret.isEmpty()) {
            this.secret = SecretKeyGenerator.generateSecretKey();
        }
    }
}

