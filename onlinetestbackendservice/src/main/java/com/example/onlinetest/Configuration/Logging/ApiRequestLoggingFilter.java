package com.example.onlinetest.Configuration.Logging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Logs a single line for every HTTP request handled by the application.
 *
 * Intentionally does NOT log request/response bodies (PII risk).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiRequestLoggingFilter.class);

    private final LoggingProperties props;

    public ApiRequestLoggingFilter(LoggingProperties props) {
        this.props = props;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!props.isHttp()) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String fullPath = query == null ? path : path + "?" + query;

        try {
            log.info("HTTP START {} {}", method, fullPath);
            filterChain.doFilter(request, response);
        } finally {
            long tookMs = System.currentTimeMillis() - start;
            int status = response.getStatus();

            String user = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                user = auth.getName();
            }

            if (user != null) {
                log.info("HTTP END {} {} -> {} ({} ms) user={}", method, fullPath, status, tookMs, user);
            } else {
                log.info("HTTP END {} {} -> {} ({} ms)", method, fullPath, status, tookMs);
            }
        }
    }
}
