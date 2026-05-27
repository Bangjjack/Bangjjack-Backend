package com.project.bangjjack.domain.post.external.aimatch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.match-api")
public record AiMatchApiProperties(
        String baseUrl,
        int connectTimeout,
        int readTimeout,
        int readTimeoutBatch,
        int recommendedTopK,
        long recommendedCacheTtl
) {
}
