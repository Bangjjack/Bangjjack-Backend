package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.application.loader.MatchAnalysisDataLoader;
import com.project.bangjjack.domain.post.application.exception.SelfMatchNotAllowedException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.service.FeatureLabelConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchAnalysisUseCase")
class MatchAnalysisUseCaseTest {

    @Mock
    private MatchAnalysisDataLoader matchAnalysisDataLoader;
    @Mock
    private MatchAnalysisPort matchAnalysisPort;
    @Mock
    private FeatureLabelConverter featureLabelConverter;

    @InjectMocks
    private MatchAnalysisUseCase matchAnalysisUseCase;

    @Nested
    @DisplayName("매칭률 분석 시")
    class AnalyzeMatchRate {

        @Test
        @DisplayName("로더로 커맨드 조립 → 포트로 분석 → 라벨 변환된 응답을 반환한다")
        void 정상_분석_요청_시_라벨_변환된_응답_반환() {
            // given
            Long requesterId = 1L;
            Long postId = 10L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            given(matchAnalysisDataLoader.loadCommand(requesterId, postId)).willReturn(command);
            given(matchAnalysisPort.analyze(command))
                    .willReturn(new MatchAnalysisResult(82, List.of("diff_sleep_time"), List.of("match_smoking", "diff_clean_freq", "match_prio1_sleep")));
            given(featureLabelConverter.toLabels(List.of("diff_sleep_time"))).willReturn(List.of("취침 시간"));
            given(featureLabelConverter.toLabels(List.of("match_smoking", "diff_clean_freq", "match_prio1_sleep")))
                    .willReturn(List.of("흡연 습관", "청소 빈도", "1순위: 수면 패턴"));

            // when
            MatchRateResponse response = matchAnalysisUseCase.analyzeMatchRate(requesterId, postId);

            // then
            assertThat(response.matchRate()).isEqualTo(82);
            assertThat(response.matchedAttributes()).containsExactly("취침 시간");
            assertThat(response.recommendedTopics()).containsExactly("흡연 습관", "청소 빈도", "1순위: 수면 패턴");
        }

        @Test
        @DisplayName("AI 응답의 matchedFeatures가 비어있어도 정상 처리한다")
        void matchedFeatures가_비어있는_케이스() {
            // given
            Long requesterId = 1L;
            Long postId = 10L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            given(matchAnalysisDataLoader.loadCommand(requesterId, postId)).willReturn(command);
            given(matchAnalysisPort.analyze(command))
                    .willReturn(new MatchAnalysisResult(50, List.of(), List.of("match_prio2_clean")));
            given(featureLabelConverter.toLabels(List.of())).willReturn(List.of());
            given(featureLabelConverter.toLabels(List.of("match_prio2_clean"))).willReturn(List.of("2순위: 청결도"));

            // when
            MatchRateResponse response = matchAnalysisUseCase.analyzeMatchRate(requesterId, postId);

            // then
            assertThat(response.matchedAttributes()).isEmpty();
            assertThat(response.recommendedTopics()).containsExactly("2순위: 청결도");
        }

        @Test
        @DisplayName("로더에서 발생한 예외는 그대로 전파된다")
        void 로더_예외_전파() {
            // given
            Long requesterId = 1L;
            Long postId = 10L;
            given(matchAnalysisDataLoader.loadCommand(requesterId, postId))
                    .willThrow(SelfMatchNotAllowedException.class);

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(SelfMatchNotAllowedException.class);
        }

        @Test
        @DisplayName("AI API 호출 실패 시 AiServiceUnavailableException이 전파된다")
        void AI_API_실패_시_예외_전파() {
            // given
            Long requesterId = 1L;
            Long postId = 10L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            given(matchAnalysisDataLoader.loadCommand(requesterId, postId)).willReturn(command);
            given(matchAnalysisPort.analyze(any(MatchAnalysisCommand.class)))
                    .willThrow(AiServiceUnavailableException.class);

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(AiServiceUnavailableException.class);
        }
    }
}
