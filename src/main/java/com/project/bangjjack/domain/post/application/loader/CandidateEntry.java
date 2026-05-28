package com.project.bangjjack.domain.post.application.loader;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;

public record CandidateEntry(
        RoommatePost post,
        MatchAnalysisProfile profile,
        int currentMemberCount
) {
    public static CandidateEntry of(RoommatePost post, MatchAnalysisProfile profile, int currentMemberCount) {
        return new CandidateEntry(post, profile, currentMemberCount);
    }
}
