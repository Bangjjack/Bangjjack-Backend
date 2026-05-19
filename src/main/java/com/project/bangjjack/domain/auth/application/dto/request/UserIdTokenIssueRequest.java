package com.project.bangjjack.domain.auth.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserIdTokenIssueRequest(
        @NotNull @Positive Long userId
) {
}
