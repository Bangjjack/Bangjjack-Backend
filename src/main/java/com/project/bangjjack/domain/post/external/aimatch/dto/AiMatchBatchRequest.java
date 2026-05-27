package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiMatchBatchRequest(
        @JsonProperty("user") AiMatchUserPayload user,
        @JsonProperty("candidates") List<AiMatchUserPayload> candidates,
        @JsonProperty("top_k") int topK
) {
    public static AiMatchBatchRequest of(AiMatchUserPayload user, List<AiMatchUserPayload> candidates, int topK) {
        return new AiMatchBatchRequest(user, candidates, topK);
    }
}
