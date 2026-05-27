package com.project.bangjjack.domain.user.application.loader;

import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.user.domain.entity.User;

public record CandidateUserEntry(
        User user,
        MatchAnalysisProfile profile
) {
    public static CandidateUserEntry of(User user, MatchAnalysisProfile profile) {
        return new CandidateUserEntry(user, profile);
    }
}
