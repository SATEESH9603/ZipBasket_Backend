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
 * Single JWT filter that enforces access rules for both admin/seller operations
 * and authenticated user endpoints.
 *
 * - Admin/Seller: POST /api/products (SELLER or ADMIN) and /api/admin/** (ADMIN)
 * - Authenticated user: /api/cart/**, /api/user/address/**, /api/user/wishlist/**, and /api/checkout (any valid token)
 */
@Component
public class JwtAccessFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;

    public JwtAccessFilter(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String ctx = request.getContextPath();
        String method = request.getMethod();

        boolean protectProductPost = "POST".equalsIgnoreCase(method) && path != null && path.startsWith(ctx + "/api/products");
        boolean protectAdminPaths = path != null && path.startsWith(ctx + "/api/admin");
        boolean protectCartPaths = path != null && path.startsWith(ctx + "/api/cart");
        boolean protectAddressPaths = path != null && path.startsWith(ctx + "/api/user/address");
        boolean protectWishlistPaths = path != null && path.startsWith(ctx + "/api/user/wishlist");
        boolean protectCheckoutPaths = path != null && (path.startsWith(ctx + "/api/checkout") || path.equals(ctx + "/api/checkout"));
        boolean protectOrdersPaths = path != null && path.startsWith(ctx + "/api/orders");

        if (protectProductPost || protectAdminPaths || protectCartPaths || protectAddressPaths || protectWishlistPaths || protectCheckoutPaths || protectOrdersPaths) {
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
                String role = jwtService.extractRole(token);
                String username = jwtService.extractUsername(token);
                if (username == null || username.isBlank()) {
                    writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: user not found");
                    return;
                }
                if (protectAdminPaths) {
                    if (role == null || !"ADMIN".equalsIgnoreCase(role)) {
                        writeError(response, HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
                        return;
                    }
                } else if (protectProductPost) {
                    if (role == null || !("ADMIN".equalsIgnoreCase(role) || "SELLER".equalsIgnoreCase(role))) {
                        writeError(response, HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
                        return;
                    }
                }
                // cart/address/wishlist/checkout only require valid token
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
