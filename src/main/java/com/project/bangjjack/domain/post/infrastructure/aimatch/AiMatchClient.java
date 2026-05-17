package com.project.bangjjack.domain.post.infrastructure.aimatch;

import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.infrastructure.aimatch.dto.AiMatchRequest;
import com.project.bangjjack.domain.post.infrastructure.aimatch.dto.AiMatchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class AiMatchClient {

    private static final String MATCH_DETAIL_PATH = "/match-detail";

    private final RestClient aiMatchRestClient;

    public AiMatchClient(RestClient aiMatchRestClient) {
        this.aiMatchRestClient = aiMatchRestClient;
    }

    public AiMatchResponse callMatchDetail(AiMatchRequest request) {
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
            log.error("AI match API failed: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AiServiceUnavailableException();
        } catch (RestClientException e) {
            log.error("AI match API call failed", e);
            throw new AiServiceUnavailableException();
        }
    }
}
