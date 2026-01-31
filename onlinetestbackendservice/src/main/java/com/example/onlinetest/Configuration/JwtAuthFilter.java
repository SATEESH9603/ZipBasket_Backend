package com.example.onlinetest.Configuration;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.onlinetest.Domain.ErrorModel;
import com.example.onlinetest.Service.JwtToken.IJwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generic JWT auth filter: validates Bearer token for user-access endpoints
 * (e.g., /api/cart/**) before reaching controllers.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;

    public JwtAuthFilter(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String context = request.getContextPath();
        boolean protectCartPaths = path != null && path.startsWith(context + "/api/cart");

        if (protectCartPaths) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isBlank()) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
                return;
            }

            String token = authHeader.trim();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            } else {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authorization header must start with Bearer");
                return;
            }

            try {
                // Basic validation: username must be present; token must be valid format
                String userName = jwtService.extractUsername(token);
                if (userName == null || userName.isBlank()) {
                    writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: user not found");
                    return;
                }
            } catch (Exception e) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorModel err = new ErrorModel();
        err.setMessage(message);
        err.setErrorCode(status == HttpServletResponse.SC_UNAUTHORIZED ? "UNAUTHORIZED" : "FORBIDDEN");
        err.setDeveloperMessage(message);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(err);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
