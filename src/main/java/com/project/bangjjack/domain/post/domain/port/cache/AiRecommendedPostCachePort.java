package com.project.bangjjack.domain.post.domain.port.cache;

import com.project.bangjjack.domain.post.application.dto.response.AiRecommendedPostResponse;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface AiRecommendedPostCachePort {

    Optional<List<AiRecommendedPostResponse>> find(Long userId, RoomSize roomSize);

    void save(Long userId, RoomSize roomSize, List<AiRecommendedPostResponse> value, Duration ttl);
}
