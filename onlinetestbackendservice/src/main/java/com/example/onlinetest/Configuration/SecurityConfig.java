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
                // Public product catalog endpoints (GET/listing). Admin/Seller actions protected by JwtAdminFilter
                "/api/products",
                "/api/products/**"
            ).permitAll()
            // All other endpoints require authentication (e.g., /api/cart/**)
            .anyRequest().authenticated()
        );

        // Keep basic auth enabled for development; JWT filters (JwtAdminFilter, JwtAuthFilter) are registered via FilterConfig
        http.httpBasic(withDefaults());

        return http.build();
    }
}
