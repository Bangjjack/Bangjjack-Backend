package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.AiRecommendedPostResponse;
import com.project.bangjjack.domain.post.application.loader.CandidateEntry;
import com.project.bangjjack.domain.post.application.loader.RecommendedPostsBundle;
import com.project.bangjjack.domain.post.application.loader.RecommendedPostsDataLoader;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.port.cache.AiRecommendedPostCachePort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchResult;
import com.project.bangjjack.domain.post.domain.port.match.RankedMatch;
import com.project.bangjjack.domain.post.external.aimatch.AiMatchApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiRecommendedPostUseCase {

    private final AiRecommendedPostCachePort aiRecommendedPostCachePort;
    private final RecommendedPostsDataLoader recommendedPostsDataLoader;
    private final MatchBatchAnalysisPort matchBatchAnalysisPort;
    private final AiMatchApiProperties aiMatchApiProperties;

    public List<AiRecommendedPostResponse> getRecommended(Long userId, RoomSize roomSize) {
        Optional<List<AiRecommendedPostResponse>> cached = aiRecommendedPostCachePort.find(userId, roomSize);
        if (cached.isPresent()) {
            return cached.get();
        }

        RecommendedPostsBundle bundle = recommendedPostsDataLoader.loadBundle(userId, roomSize);
        List<CandidateEntry> candidates = bundle.candidates();

        Duration ttl = Duration.ofSeconds(aiMatchApiProperties.recommendedCacheTtl());

        if (candidates.isEmpty()) {
            List<AiRecommendedPostResponse> empty = List.of();
            aiRecommendedPostCachePort.save(userId, roomSize, empty, ttl);
            return empty;
        }

        MatchBatchCommand command = MatchBatchCommand.of(
                bundle.requesterProfile(),
                candidates.stream().map(CandidateEntry::profile).toList(),
                aiMatchApiProperties.recommendedTopK()
        );
        MatchBatchResult result = matchBatchAnalysisPort.analyze(command);

        List<AiRecommendedPostResponse> responses = result.ranked().stream()
                .map(ranked -> toResponse(ranked, candidates))
                .toList();

        aiRecommendedPostCachePort.save(userId, roomSize, responses, ttl);
        return responses;
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
