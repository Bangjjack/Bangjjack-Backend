package com.project.bangjjack.global.config.security;

import org.springframework.stereotype.Component;

@Component
public class PermitUrlConfig {

    public String[] publicUrls() {
        return new String[]{
                "/oauth2/authorization/**",
                "/auth/login/**",
                "/api/v1/auth/token",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**"
        };
    }
}
