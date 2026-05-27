package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.port.match.SummaryComment;

public record MatchSummaryCommentResponse(
        String brief,
        String positive,
        String caution
) {
    public static MatchSummaryCommentResponse from(SummaryComment comment) {
        return new MatchSummaryCommentResponse(comment.brief(), comment.positive(), comment.caution());
    }
}
