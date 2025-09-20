package com.example.onlinetest.Service.JwtToken;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static String generateSecretKey() {
        byte[] key = new byte[64]; // 512-bit
        new SecureRandom().nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        return secret;        
    }
}
