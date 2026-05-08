package com.project.bangjjack.domain.chat.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendChatMessageRequest(
        @NotBlank @Size(max = 1000) String content,
        @NotBlank String clientTempId
) {
}
