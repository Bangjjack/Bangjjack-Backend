package com.project.bangjjack.domain.auth.application.dto.response;

public record WsTokenResponse(String wsToken) {

    public static WsTokenResponse of(String wsToken) {
        return new WsTokenResponse(wsToken);
    }
}
