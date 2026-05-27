package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.ConversationStarterResponse;
import com.project.bangjjack.domain.post.application.dto.response.MatchCountsResponse;
import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.dto.response.MatchSummaryCommentResponse;
import com.project.bangjjack.domain.post.application.dto.response.MatchedFeatureResponse;
import com.project.bangjjack.domain.post.application.dto.response.MismatchedFeatureResponse;
import com.project.bangjjack.domain.post.application.dto.response.TopInfluentialFeatureResponse;
import com.project.bangjjack.domain.post.application.loader.MatchAnalysisDataLoader;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.service.FeatureLabelConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MatchAnalysisUseCase {

    private final MatchAnalysisDataLoader matchAnalysisDataLoader;
    private final MatchAnalysisPort matchAnalysisPort;
    private final FeatureLabelConverter featureLabelConverter;

    public MatchRateResponse analyzeMatchRate(Long userId, Long postId) {
        MatchAnalysisCommand command = matchAnalysisDataLoader.loadCommand(userId, postId);
        MatchAnalysisResult result = matchAnalysisPort.analyze(command);

        List<String> topKeys = result.topInfluentialFeatures();
        List<String> topLabels = featureLabelConverter.toLabels(topKeys);
        List<TopInfluentialFeatureResponse> topInfluential = zipToTopInfluential(topKeys, topLabels);

        return MatchRateResponse.of(
                result.matchRate(),
                MatchCountsResponse.from(result.counts()),
                result.matchedFeatures().stream().map(MatchedFeatureResponse::from).toList(),
                result.mismatchedFeatures().stream().map(MismatchedFeatureResponse::from).toList(),
                result.conversationStarters().stream().map(ConversationStarterResponse::from).toList(),
                topInfluential,
                MatchSummaryCommentResponse.from(result.summaryComment())
        );
    }

    private List<TopInfluentialFeatureResponse> zipToTopInfluential(List<String> keys, List<String> labels) {
        return IntStream.range(0, keys.size())
                .mapToObj(i -> TopInfluentialFeatureResponse.of(keys.get(i), labels.get(i)))
                .toList();
    }
}
