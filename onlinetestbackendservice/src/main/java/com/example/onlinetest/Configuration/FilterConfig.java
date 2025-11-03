package com.example.onlinetest.Configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAdminFilter> jwtAdminFilterRegistration(JwtAdminFilter filter) {
        FilterRegistrationBean<JwtAdminFilter> reg = new FilterRegistrationBean<>(filter);
        // register for product creation endpoint
        reg.addUrlPatterns("/api/products");
        reg.setName("jwtAdminFilter");
        // Ensure it runs early
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

}
