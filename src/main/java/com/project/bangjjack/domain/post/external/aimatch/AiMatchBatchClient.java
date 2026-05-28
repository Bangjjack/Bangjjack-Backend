package com.project.bangjjack.domain.post.external.aimatch;

import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchResult;
import com.project.bangjjack.domain.post.domain.port.match.RankedMatch;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchBatchRequest;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchBatchResponse;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchUserPayload;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiRankedItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class AiMatchBatchClient implements MatchBatchAnalysisPort {

    private static final String MATCH_DETAIL_BATCH_PATH = "/match-detail-batch";

    private final RestClient aiMatchBatchRestClient;

    public AiMatchBatchClient(@Qualifier("aiMatchBatchRestClient") RestClient aiMatchBatchRestClient) {
        this.aiMatchBatchRestClient = aiMatchBatchRestClient;
    }

    @Override
    public MatchBatchResult analyze(MatchBatchCommand command) {
        int candidateCount = command.candidates().size();
        AiMatchBatchRequest request = AiMatchBatchRequest.of(
                toPayload(command.requester()),
                command.candidates().stream().map(this::toPayload).toList(),
                command.topK()
        );
        AiMatchBatchResponse response = callMatchDetailBatch(request, candidateCount);
        return toResult(response, candidateCount);
    }

    private AiMatchUserPayload toPayload(MatchAnalysisProfile profile) {
        return AiMatchUserPayload.of(profile.user(), profile.checklist(), profile.sleepHabits(), profile.preference());
    }

    private MatchBatchResult toResult(AiMatchBatchResponse response, int candidateCount) {
        List<AiRankedItem> ranked = response.ranked();
        if (ranked == null) {
            log.warn("AI match batch API returned null ranked list");
            throw new AiServiceUnavailableException();
        }
        return MatchBatchResult.of(
                ranked.stream()
                        .map(item -> toRankedMatch(item, candidateCount))
                        .toList()
        );
    }

    private RankedMatch toRankedMatch(AiRankedItem item, int candidateCount) {
        int candidateIndex = item.candidateIndex();
        if (candidateIndex < 0 || candidateIndex >= candidateCount) {
            log.warn("AI match batch returned out-of-range candidateIndex: {} (candidateCount={})",
                    candidateIndex, candidateCount);
            throw new AiServiceUnavailableException();
        }
        return RankedMatch.of(item.rank(), candidateIndex, item.matchRate());
    }

    private AiMatchBatchResponse callMatchDetailBatch(AiMatchBatchRequest request, int candidateCount) {
        try {
            log.info("AI match batch API call: candidates={}", candidateCount);
            AiMatchBatchResponse response = aiMatchBatchRestClient.post()
                    .uri(MATCH_DETAIL_BATCH_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiMatchBatchResponse.class);

            if (response == null) {
                log.warn("AI match batch API returned null body");
                throw new AiServiceUnavailableException();
            }
            return response;
        } catch (HttpStatusCodeException e) {
            log.error("AI match batch API failed: status={}", e.getStatusCode());
            throw new AiServiceUnavailableException();
        } catch (RestClientException e) {
            log.error("AI match batch API call failed", e);
            throw new AiServiceUnavailableException();
        }
    }
}
