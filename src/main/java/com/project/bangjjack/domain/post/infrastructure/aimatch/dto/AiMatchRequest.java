package com.project.bangjjack.domain.post.infrastructure.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiMatchRequest(
        @JsonProperty("user_a") AiMatchUserPayload userA,
        @JsonProperty("user_b") AiMatchUserPayload userB
) {
    public static AiMatchRequest of(AiMatchUserPayload userA, AiMatchUserPayload userB) {
        return new AiMatchRequest(userA, userB);
    }
}
