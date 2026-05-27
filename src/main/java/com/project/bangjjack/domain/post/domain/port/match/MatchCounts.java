package com.project.bangjjack.domain.post.domain.port.match;

public record MatchCounts(
        int matched,
        int mismatched,
        int total
) {
    public static MatchCounts of(int matched, int mismatched, int total) {
        return new MatchCounts(matched, mismatched, total);
    }
}
