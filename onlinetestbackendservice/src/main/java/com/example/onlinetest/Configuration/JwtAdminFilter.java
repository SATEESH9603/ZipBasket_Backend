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

@Component
public class JwtAdminFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;

    public JwtAdminFilter(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Only enforce for POST /api/products
        String path = request.getRequestURI();
        String method = request.getMethod();
        // Enforce for POST /api/products and any /api/admin/** endpoints (admin operations)
        boolean protectProductPost = "POST".equalsIgnoreCase(method) && path != null && path.startsWith(request.getContextPath() + "/api/products");
        boolean protectAdminPaths = path != null && path.startsWith(request.getContextPath() + "/api/admin");
        if (protectProductPost || protectAdminPaths) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isBlank()) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
                return;
            }

            String token = authHeader.trim();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }

            try {
                String role = jwtService.extractRole(token);
                if ((role == null || !"ADMIN".equalsIgnoreCase(role)) && (protectProductPost && !"SELLER".equalsIgnoreCase(role))) {
                    writeError(response, HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions: ");
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
        // Build ErrorModel and serialize using Jackson for consistent error structure
        ErrorModel err = new ErrorModel();
        err.setMessage(message);
        // Use a generic error code for auth failures; callers can customize
        err.setErrorCode(status == HttpServletResponse.SC_UNAUTHORIZED ? "UNAUTHORIZED" : "FORBIDDEN");
        err.setDeveloperMessage(message);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(err);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

}
