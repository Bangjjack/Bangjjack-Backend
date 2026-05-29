package com.project.bangjjack.domain.user.external.aimatch;

import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.domain.port.match.ConversationStarter;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.port.match.MatchCounts;
import com.project.bangjjack.domain.post.domain.port.match.MatchedFeature;
import com.project.bangjjack.domain.post.domain.port.match.MismatchedFeature;
import com.project.bangjjack.domain.post.domain.port.match.SummaryComment;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiConversationStarter;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchCounts;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchRequest;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchResponse;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchUserPayload;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchedFeature;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMismatchedFeature;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiSummaryComment;
import com.project.bangjjack.domain.user.domain.port.matchreport.MatchReportPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class AiMatchReportClient implements MatchReportPort {

    private static final String MATCH_REPORT_PATH = "/match-detail";

    private final RestClient aiMatchRestClient;

    public AiMatchReportClient(RestClient aiMatchRestClient) {
        this.aiMatchRestClient = aiMatchRestClient;
    }

    @Override
    public MatchAnalysisResult analyze(MatchAnalysisCommand command) {
        AiMatchRequest request = AiMatchRequest.of(toPayload(command.requester()), toPayload(command.author()));
        AiMatchResponse response = callMatchReport(request);
        return toResult(response);
    }

    private AiMatchUserPayload toPayload(MatchAnalysisProfile profile) {
        return AiMatchUserPayload.of(profile.user(), profile.checklist(), profile.sleepHabits(), profile.preference());
    }

    private MatchAnalysisResult toResult(AiMatchResponse response) {
        return MatchAnalysisResult.of(
                response.matchRate(),
                toCounts(response.counts()),
                toMatchedFeatures(response.matchedFeatures()),
                toMismatchedFeatures(response.mismatchedFeatures()),
                toConversationStarters(response.conversationStarters()),
                response.topInfluentialFeatures() == null ? List.of() : response.topInfluentialFeatures(),
                toSummaryComment(response.summaryComment())
        );
    }

    private MatchCounts toCounts(AiMatchCounts counts) {
        if (counts == null) {
            return MatchCounts.of(0, 0, 0);
        }
        return MatchCounts.of(counts.matched(), counts.mismatched(), counts.total());
    }

    private List<MatchedFeature> toMatchedFeatures(List<AiMatchedFeature> features) {
        if (features == null) {
            return List.of();
        }
        return features.stream()
                .map(f -> MatchedFeature.of(f.key(), f.label(), f.description()))
                .toList();
    }

    private List<MismatchedFeature> toMismatchedFeatures(List<AiMismatchedFeature> features) {
        if (features == null) {
            return List.of();
        }
        return features.stream()
                .map(f -> MismatchedFeature.of(f.key(), f.label(), f.description(), f.advice()))
                .toList();
    }

    private List<ConversationStarter> toConversationStarters(List<AiConversationStarter> starters) {
        if (starters == null) {
            return List.of();
        }
        return starters.stream()
                .map(s -> ConversationStarter.of(s.key(), s.starter(), s.subtitle()))
                .toList();
    }

    private SummaryComment toSummaryComment(AiSummaryComment comment) {
        if (comment == null) {
            return SummaryComment.of("", "", "");
        }
        return SummaryComment.of(
                comment.brief() == null ? "" : comment.brief(),
                comment.positive() == null ? "" : comment.positive(),
                comment.caution() == null ? "" : comment.caution()
        );
    }

    private AiMatchResponse callMatchReport(AiMatchRequest request) {
        try {
            AiMatchResponse response = aiMatchRestClient.post()
                    .uri(MATCH_REPORT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiMatchResponse.class);

            if (response == null) {
                log.warn("AI match-report API returned null body");
                throw new AiServiceUnavailableException();
            }
            return response;
        } catch (HttpStatusCodeException e) {
            log.error("AI match-report API failed: status={}", e.getStatusCode());
            throw new AiServiceUnavailableException();
        } catch (RestClientException e) {
            log.error("AI match-report API call failed", e);
            throw new AiServiceUnavailableException();
        }
    }
}
