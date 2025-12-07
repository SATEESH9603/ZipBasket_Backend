package com.example.onlinetest.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/api/auth/**",
                "/api/login",
                "/api/register",
                "/api/users/register",
                "/api/products",
                "/api/products/****",
                "/api/admin/**"
            ).permitAll()
            // other endpoints require authentication
            .anyRequest().authenticated()
        );

        // keep basic auth enabled for development; replace with JWT filter later
        http.httpBasic(withDefaults());

        return http.build();
    }
}
