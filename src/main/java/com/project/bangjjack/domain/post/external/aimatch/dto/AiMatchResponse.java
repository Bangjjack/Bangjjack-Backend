package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiMatchResponse(
        int matchRate,
        List<String> matchedFeatures,
        List<String> topInfluentialFeatures
) {
}
