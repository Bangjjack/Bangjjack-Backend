package com.project.bangjjack.domain.post.application.loader;

import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;

import java.util.List;

public record RecommendedPostsBundle(
        MatchAnalysisProfile requesterProfile,
        List<CandidateEntry> candidates
) {
    public static RecommendedPostsBundle of(MatchAnalysisProfile requesterProfile, List<CandidateEntry> candidates) {
        return new RecommendedPostsBundle(requesterProfile, candidates);
    }
}
