package com.project.bangjjack.global.config.websocket;

import java.security.Principal;

public record UserPrincipal(Long userId) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
