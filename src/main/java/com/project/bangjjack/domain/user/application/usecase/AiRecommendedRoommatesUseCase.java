package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

@Service
public class AiRecommendedRoommatesUseCase {

    private final AiRecommendedRoommatesCachePort aiRecommendedRoommatesCachePort;
    private final RecommendedRoommatesDataLoader recommendedRoommatesDataLoader;
    private final MatchBatchAnalysisPort matchBatchAnalysisPort;
    private final AiMatchApiProperties aiMatchApiProperties;
    private final Executor aiMatchTaskExecutor;

    public AiRecommendedRoommatesUseCase(
            AiRecommendedRoommatesCachePort aiRecommendedRoommatesCachePort,
            RecommendedRoommatesDataLoader recommendedRoommatesDataLoader,
            MatchBatchAnalysisPort matchBatchAnalysisPort,
            AiMatchApiProperties aiMatchApiProperties,
            @Qualifier("aiMatchTaskExecutor") Executor aiMatchTaskExecutor
    ) {
        this.aiRecommendedRoommatesCachePort = aiRecommendedRoommatesCachePort;
        this.recommendedRoommatesDataLoader = recommendedRoommatesDataLoader;
        this.matchBatchAnalysisPort = matchBatchAnalysisPort;
        this.aiMatchApiProperties = aiMatchApiProperties;
        this.aiMatchTaskExecutor = aiMatchTaskExecutor;
    }

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

        int topK = aiMatchApiProperties.recommendedRoommatesTopK();
        int chunkSize = aiMatchApiProperties.recommendedRoommatesChunkSize();

        MatchBatchResult result = analyze(bundle.requesterProfile(), candidates, topK, chunkSize);

        List<AiRecommendedRoommateResponse> responses = result.ranked().stream()
                .map(ranked -> toResponse(ranked, candidates))
                .toList();

        aiRecommendedRoommatesCachePort.save(userId, responses, ttl);
        return responses;
    }

    private MatchBatchResult analyze(
            MatchAnalysisProfile requester,
            List<CandidateUserEntry> candidates,
            int topK,
            int chunkSize
    ) {
        if (chunkSize <= 0 || candidates.size() <= chunkSize) {
            MatchBatchCommand command = MatchBatchCommand.of(
                    requester,
                    candidates.stream().map(CandidateUserEntry::profile).toList(),
                    topK
            );
            return matchBatchAnalysisPort.analyze(command);
        }

        return analyzeInParallel(requester, candidates, topK, chunkSize);
    }

    private MatchBatchResult analyzeInParallel(
            MatchAnalysisProfile requester,
            List<CandidateUserEntry> candidates,
            int topK,
            int chunkSize
    ) {
        int totalSize = candidates.size();
        int chunkCount = chunkCount(totalSize, chunkSize);

        List<CompletableFuture<List<RankedMatch>>> futures = new ArrayList<>(chunkCount);
        for (int i = 0; i < chunkCount; i++) {
            int startIndex = i * chunkSize;
            int endIndex = Math.min(startIndex + chunkSize, totalSize);
            List<MatchAnalysisProfile> chunkProfiles = candidates.subList(startIndex, endIndex).stream()
                    .map(CandidateUserEntry::profile)
                    .toList();
            int perChunkTopK = Math.min(chunkProfiles.size(), topK);
            CompletableFuture<List<RankedMatch>> future = CompletableFuture.supplyAsync(() -> {
                MatchBatchCommand command = MatchBatchCommand.of(requester, chunkProfiles, perChunkTopK);
                MatchBatchResult chunkResult = matchBatchAnalysisPort.analyze(command);
                return chunkResult.ranked().stream()
                        .map(rm -> RankedMatch.of(rm.rank(), startIndex + rm.candidateIndex(), rm.matchRate()))
                        .toList();
            }, aiMatchTaskExecutor);
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            throw new AiServiceUnavailableException();
        }

        List<RankedMatch> sorted = futures.stream()
                .flatMap(f -> f.join().stream())
                .sorted(Comparator
                        .comparingInt(RankedMatch::matchRate).reversed()
                        .thenComparingInt(RankedMatch::candidateIndex))
                .limit(topK)
                .toList();

        List<RankedMatch> reranked = IntStream.range(0, sorted.size())
                .mapToObj(i -> RankedMatch.of(i + 1, sorted.get(i).candidateIndex(), sorted.get(i).matchRate()))
                .toList();

        return MatchBatchResult.of(reranked);
    }

    private int chunkCount(int totalSize, int chunkSize) {
        return (totalSize + chunkSize - 1) / chunkSize;
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
