package com.project.bangjjack.domain.post.domain.port.match;

public record ConversationStarter(
        String key,
        String starter,
        String subtitle
) {
    public static ConversationStarter of(String key, String starter, String subtitle) {
        return new ConversationStarter(key, starter, subtitle);
    }
}
