package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.loader.MatchAnalysisDataLoader;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.service.FeatureLabelConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchAnalysisUseCase {

    private final MatchAnalysisDataLoader matchAnalysisDataLoader;
    private final MatchAnalysisPort matchAnalysisPort;
    private final FeatureLabelConverter featureLabelConverter;

    public MatchRateResponse analyzeMatchRate(Long userId, Long postId) {
        MatchAnalysisCommand command = matchAnalysisDataLoader.loadCommand(userId, postId);
        MatchAnalysisResult result = matchAnalysisPort.analyze(command);
        return MatchRateResponse.of(
                result.matchRate(),
                featureLabelConverter.toLabels(result.matchedFeatures()),
                featureLabelConverter.toLabels(result.topInfluentialFeatures())
        );
    }
}
