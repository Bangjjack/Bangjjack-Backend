package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.post.domain.port.match.MatchBatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchResult;
import com.project.bangjjack.domain.post.domain.port.match.RankedMatch;
import com.project.bangjjack.domain.post.external.aimatch.AiMatchApiProperties;
import com.project.bangjjack.domain.user.application.dto.response.AiRecommendedRoommateResponse;
import com.project.bangjjack.domain.user.application.loader.CandidateUserEntry;
import com.project.bangjjack.domain.user.application.loader.RecommendedRoommatesBundle;
import com.project.bangjjack.domain.user.application.loader.RecommendedRoommatesDataLoader;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.port.cache.AiRecommendedRoommatesCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiRecommendedRoommatesUseCase {

    private final AiRecommendedRoommatesCachePort aiRecommendedRoommatesCachePort;
    private final RecommendedRoommatesDataLoader recommendedRoommatesDataLoader;
    private final MatchBatchAnalysisPort matchBatchAnalysisPort;
    private final AiMatchApiProperties aiMatchApiProperties;

    public List<AiRecommendedRoommateResponse> getRecommended(Long userId) {
        Optional<List<AiRecommendedRoommateResponse>> cached = aiRecommendedRoommatesCachePort.find(userId);
        if (cached.isPresent()) {
            return cached.get();
        }

        RecommendedRoommatesBundle bundle = recommendedRoommatesDataLoader.loadBundle(userId);
        List<CandidateUserEntry> candidates = bundle.candidates();

        Duration ttl = Duration.ofSeconds(aiMatchApiProperties.recommendedRoommatesCacheTtl());

        if (candidates.isEmpty()) {
            List<AiRecommendedRoommateResponse> empty = List.of();
            aiRecommendedRoommatesCachePort.save(userId, empty, ttl);
            return empty;
        }

        MatchBatchCommand command = MatchBatchCommand.of(
                bundle.requesterProfile(),
                candidates.stream().map(CandidateUserEntry::profile).toList(),
                aiMatchApiProperties.recommendedRoommatesTopK()
        );
        MatchBatchResult result = matchBatchAnalysisPort.analyze(command);

        List<AiRecommendedRoommateResponse> responses = result.ranked().stream()
                .map(ranked -> toResponse(ranked, candidates))
                .toList();

        aiRecommendedRoommatesCachePort.save(userId, responses, ttl);
        return responses;
    }

    private AiRecommendedRoommateResponse toResponse(RankedMatch ranked, List<CandidateUserEntry> candidates) {
        CandidateUserEntry entry = candidates.get(ranked.candidateIndex());
        User user = entry.user();
        return AiRecommendedRoommateResponse.from(
                user,
                user.getDepartment(),
                entry.profile().checklist(),
                entry.profile().preference(),
                ranked.matchRate()
        );
    }
}
