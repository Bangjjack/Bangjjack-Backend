package com.project.bangjjack.domain.chat.application.dto.response;

public record AuthSuccessResponse(String type, Long userId) {

    public static AuthSuccessResponse of(Long userId) {
        return new AuthSuccessResponse("AUTH_SUCCESS", userId);
    }
}
