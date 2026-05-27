package com.project.bangjjack.domain.user.domain.port.cache;

import com.project.bangjjack.domain.user.application.dto.response.AiRecommendedRoommateResponse;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface AiRecommendedRoommatesCachePort {

    Optional<List<AiRecommendedRoommateResponse>> find(Long userId);

    void save(Long userId, List<AiRecommendedRoommateResponse> value, Duration ttl);
}
