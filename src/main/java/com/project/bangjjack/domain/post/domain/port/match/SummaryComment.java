package com.project.bangjjack.domain.post.domain.port.match;

public record SummaryComment(
        String brief,
        String positive,
        String caution
) {
    public static SummaryComment of(String brief, String positive, String caution) {
        return new SummaryComment(brief, positive, caution);
    }
}
