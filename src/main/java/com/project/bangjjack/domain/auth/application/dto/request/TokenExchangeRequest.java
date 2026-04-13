package com.project.bangjjack.domain.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenExchangeRequest(
        @NotBlank String code
) {
}
