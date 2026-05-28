package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.application.loader.MatchAnalysisDataLoader;
import com.project.bangjjack.domain.post.application.exception.SelfMatchNotAllowedException;
import com.project.bangjjack.domain.post.domain.port.match.ConversationStarter;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.port.match.MatchCounts;
import com.project.bangjjack.domain.post.domain.port.match.MatchedFeature;
import com.project.bangjjack.domain.post.domain.port.match.MismatchedFeature;
import com.project.bangjjack.domain.post.domain.port.match.SummaryComment;
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
        @DisplayName("AI 응답의 matched/mismatched/conversation/summary를 그대로 전달하고, topInfluentialFeatures만 라벨을 매핑한다")
        void 정상_분석_요청_시_새로운_구조로_응답한다() {
            // given
            Long requesterId = 1L;
            Long postId = 10L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            MatchAnalysisResult result = new MatchAnalysisResult(
                    82,
                    MatchCounts.of(6, 2, 8),
                    List.of(MatchedFeature.of("bedtime", "취침 시간", "둘 다 24~2시로 자는 편이에요")),
                    List.of(MismatchedFeature.of("clean_freq", "청소 주기", "청소 주기가 달라요", "미리 이야기 나눠보세요")),
                    List.of(ConversationStarter.of("diff_clean_freq", "\"청소 보통 얼마나 자주 해?\"", "청소 습관과 분담을 미리 맞춰봐요")),
                    List.of("diff_clean_freq", "match_smoking", "diff_sleep_time"),
                    SummaryComment.of("브리프", "포지티브", "코션")
            );

            given(matchAnalysisDataLoader.loadCommand(requesterId, postId)).willReturn(command);
            given(matchAnalysisPort.analyze(command)).willReturn(result);
            given(featureLabelConverter.toLabels(List.of("diff_clean_freq", "match_smoking", "diff_sleep_time")))
                    .willReturn(List.of("청소 빈도", "흡연 습관", "취침 시간"));

            // when
            MatchRateResponse response = matchAnalysisUseCase.analyzeMatchRate(requesterId, postId);

            // then
            assertThat(response.matchRate()).isEqualTo(82);
            assertThat(response.counts().matched()).isEqualTo(6);
            assertThat(response.counts().mismatched()).isEqualTo(2);
            assertThat(response.counts().total()).isEqualTo(8);
            assertThat(response.matchedFeatures()).hasSize(1);
            assertThat(response.matchedFeatures().get(0).label()).isEqualTo("취침 시간");
            assertThat(response.matchedFeatures().get(0).description()).isEqualTo("둘 다 24~2시로 자는 편이에요");
            assertThat(response.mismatchedFeatures()).hasSize(1);
            assertThat(response.mismatchedFeatures().get(0).advice()).isEqualTo("미리 이야기 나눠보세요");
            assertThat(response.conversationStarters()).hasSize(1);
            assertThat(response.conversationStarters().get(0).starter()).contains("청소 보통");
            assertThat(response.topInfluentialFeatures()).hasSize(3);
            assertThat(response.topInfluentialFeatures().get(0).key()).isEqualTo("diff_clean_freq");
            assertThat(response.topInfluentialFeatures().get(0).label()).isEqualTo("청소 빈도");
            assertThat(response.summaryComment().brief()).isEqualTo("브리프");
            assertThat(response.summaryComment().caution()).isEqualTo("코션");
        }

        @Test
        @DisplayName("matchedFeatures가 비어있어도 빈 리스트로 응답한다")
        void matchedFeatures가_비어있는_케이스() {
            // given
            Long requesterId = 1L;
            Long postId = 10L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            MatchAnalysisResult result = new MatchAnalysisResult(
                    50,
                    MatchCounts.of(0, 8, 8),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of("match_prio2_clean"),
                    SummaryComment.of("차이 많음", "", "")
            );

            given(matchAnalysisDataLoader.loadCommand(requesterId, postId)).willReturn(command);
            given(matchAnalysisPort.analyze(command)).willReturn(result);
            given(featureLabelConverter.toLabels(List.of("match_prio2_clean"))).willReturn(List.of("2순위: 청결도"));

            // when
            MatchRateResponse response = matchAnalysisUseCase.analyzeMatchRate(requesterId, postId);

            // then
            assertThat(response.matchedFeatures()).isEmpty();
            assertThat(response.mismatchedFeatures()).isEmpty();
            assertThat(response.conversationStarters()).isEmpty();
            assertThat(response.topInfluentialFeatures()).hasSize(1);
            assertThat(response.topInfluentialFeatures().get(0).key()).isEqualTo("match_prio2_clean");
            assertThat(response.topInfluentialFeatures().get(0).label()).isEqualTo("2순위: 청결도");
            assertThat(response.summaryComment().caution()).isEmpty();
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
