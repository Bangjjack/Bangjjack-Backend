package com.project.bangjjack.domain.post.domain.port.match;

public record RankedMatch(
        int rank,
        int candidateIndex,
        int matchRate
) {
    public static RankedMatch of(int rank, int candidateIndex, int matchRate) {
        return new RankedMatch(rank, candidateIndex, matchRate);
    }
}
