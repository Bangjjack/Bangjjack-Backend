package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.AiRecommendedPostResponse;
import com.project.bangjjack.domain.post.application.loader.CandidateEntry;
import com.project.bangjjack.domain.post.application.loader.RecommendedPostsBundle;
import com.project.bangjjack.domain.post.application.loader.RecommendedPostsDataLoader;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchResult;
import com.project.bangjjack.domain.post.domain.port.match.RankedMatch;
import com.project.bangjjack.domain.post.external.aimatch.AiMatchApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRecommendedPostUseCase {

    private final RecommendedPostsDataLoader recommendedPostsDataLoader;
    private final MatchBatchAnalysisPort matchBatchAnalysisPort;
    private final AiMatchApiProperties aiMatchApiProperties;

    public List<AiRecommendedPostResponse> getRecommended(Long userId, RoomSize roomSize) {
        RecommendedPostsBundle bundle = recommendedPostsDataLoader.loadBundle(userId, roomSize);
        List<CandidateEntry> candidates = bundle.candidates();

        if (candidates.isEmpty()) {
            return List.of();
        }

        MatchBatchCommand command = MatchBatchCommand.of(
                bundle.requesterProfile(),
                candidates.stream().map(CandidateEntry::profile).toList(),
                aiMatchApiProperties.recommendedTopK()
        );
        MatchBatchResult result = matchBatchAnalysisPort.analyze(command);

        return result.ranked().stream()
                .map(ranked -> toResponse(ranked, candidates))
                .toList();
    }

    private AiRecommendedPostResponse toResponse(RankedMatch ranked, List<CandidateEntry> candidates) {
        CandidateEntry entry = candidates.get(ranked.candidateIndex());
        return AiRecommendedPostResponse.from(
                entry.post(),
                entry.profile().checklist().getSmoking(),
                entry.currentMemberCount(),
                ranked.matchRate()
        );
    }
}
