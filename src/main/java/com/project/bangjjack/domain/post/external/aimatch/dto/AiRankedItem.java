package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiRankedItem(
        @JsonProperty("rank") int rank,
        @JsonProperty("candidateIndex") int candidateIndex,
        @JsonProperty("matchRate") int matchRate
) {
}
