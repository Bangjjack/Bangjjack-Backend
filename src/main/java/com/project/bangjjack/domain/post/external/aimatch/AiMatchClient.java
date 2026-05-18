package com.project.bangjjack.domain.post.external.aimatch;

import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchRequest;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchResponse;
import com.project.bangjjack.domain.post.external.aimatch.dto.AiMatchUserPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class AiMatchClient implements MatchAnalysisPort {

    private static final String MATCH_DETAIL_PATH = "/match-detail";

    private final RestClient aiMatchRestClient;

    public AiMatchClient(RestClient aiMatchRestClient) {
        this.aiMatchRestClient = aiMatchRestClient;
    }

    @Override
    public MatchAnalysisResult analyze(MatchAnalysisCommand command) {
        AiMatchRequest request = AiMatchRequest.of(toPayload(command.requester()), toPayload(command.author()));
        AiMatchResponse response = callMatchDetail(request);
        return MatchAnalysisResult.of(
                response.matchRate(),
                response.matchedFeatures(),
                response.topInfluentialFeatures()
        );
    }

    private AiMatchUserPayload toPayload(MatchAnalysisProfile profile) {
        return AiMatchUserPayload.of(profile.user(), profile.checklist(), profile.sleepHabits(), profile.preference());
    }

    private AiMatchResponse callMatchDetail(AiMatchRequest request) {
        try {
            AiMatchResponse response = aiMatchRestClient.post()
                    .uri(MATCH_DETAIL_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiMatchResponse.class);

            if (response == null) {
                log.warn("AI match API returned null body");
                throw new AiServiceUnavailableException();
            }
            return response;
        } catch (HttpStatusCodeException e) {
            log.error("AI match API failed: status={}", e.getStatusCode());
            throw new AiServiceUnavailableException();
        } catch (RestClientException e) {
            log.error("AI match API call failed", e);
            throw new AiServiceUnavailableException();
        }
    }
}
