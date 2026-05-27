package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.port.match.ConversationStarter;

public record ConversationStarterResponse(
        String key,
        String starter,
        String subtitle
) {
    public static ConversationStarterResponse from(ConversationStarter starter) {
        return new ConversationStarterResponse(starter.key(), starter.starter(), starter.subtitle());
    }
}
