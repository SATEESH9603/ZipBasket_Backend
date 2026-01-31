package com.example.onlinetest.Configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAccessFilter> jwtAccessFilterRegistration(JwtAccessFilter filter) {
        FilterRegistrationBean<JwtAccessFilter> reg = new FilterRegistrationBean<>(filter);
        // Register for product, admin, cart, address, wishlist, checkout, and orders endpoints
        reg.addUrlPatterns("/api/products", "/api/admin/*", "/api/cart/*", "/api/user/address/*", "/api/user/wishlist/*", "/api/checkout", "/api/orders/*");
        reg.setName("jwtAccessFilter");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}
