package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiMatchedFeature(
        String key,
        String label,
        String description
) {
}
