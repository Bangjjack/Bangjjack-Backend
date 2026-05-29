package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.application.exception.SelfMatchNotAllowedException;
import com.project.bangjjack.domain.post.domain.port.match.ConversationStarter;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.port.match.MatchCounts;
import com.project.bangjjack.domain.post.domain.port.match.MatchedFeature;
import com.project.bangjjack.domain.post.domain.port.match.MismatchedFeature;
import com.project.bangjjack.domain.post.domain.port.match.SummaryComment;
import com.project.bangjjack.domain.post.domain.service.FeatureLabelConverter;
import com.project.bangjjack.domain.user.application.loader.MatchReportDataLoader;
import com.project.bangjjack.domain.user.domain.port.matchreport.MatchReportPort;
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
@DisplayName("MatchReportUseCase")
class MatchReportUseCaseTest {

    @Mock
    private MatchReportDataLoader matchReportDataLoader;
    @Mock
    private MatchReportPort matchReportPort;
    @Mock
    private FeatureLabelConverter featureLabelConverter;

    @InjectMocks
    private MatchReportUseCase matchReportUseCase;

    @Nested
    @DisplayName("매칭 리포트 분석 시")
    class AnalyzeMatchReport {

        @Test
        @DisplayName("AI 응답의 matched/mismatched/conversation/summary를 그대로 전달하고, topInfluentialFeatures만 라벨을 매핑한다")
        void 정상_분석_요청_시_응답을_변환한다() {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            MatchAnalysisResult result = new MatchAnalysisResult(
                    78,
                    MatchCounts.of(5, 3, 8),
                    List.of(MatchedFeature.of("bedtime", "취침 시간", "둘 다 24~2시")),
                    List.of(MismatchedFeature.of("clean_freq", "청소 주기", "청소 주기가 달라요", "미리 이야기 나눠보세요")),
                    List.of(ConversationStarter.of("diff_clean_freq", "\"청소 보통 얼마나 자주 해?\"", "청소 습관과 분담을 미리 맞춰봐요")),
                    List.of("diff_clean_freq", "match_smoking"),
                    SummaryComment.of("브리프", "포지티브", "코션")
            );

            given(matchReportDataLoader.loadCommand(requesterId, targetUserId)).willReturn(command);
            given(matchReportPort.analyze(command)).willReturn(result);
            given(featureLabelConverter.toLabels(List.of("diff_clean_freq", "match_smoking")))
                    .willReturn(List.of("청소 빈도", "흡연 습관"));

            // when
            MatchRateResponse response = matchReportUseCase.analyzeMatchReport(requesterId, targetUserId);

            // then
            assertThat(response.matchRate()).isEqualTo(78);
            assertThat(response.counts().matched()).isEqualTo(5);
            assertThat(response.counts().mismatched()).isEqualTo(3);
            assertThat(response.counts().total()).isEqualTo(8);
            assertThat(response.matchedFeatures()).hasSize(1);
            assertThat(response.matchedFeatures().get(0).label()).isEqualTo("취침 시간");
            assertThat(response.mismatchedFeatures()).hasSize(1);
            assertThat(response.mismatchedFeatures().get(0).advice()).isEqualTo("미리 이야기 나눠보세요");
            assertThat(response.conversationStarters()).hasSize(1);
            assertThat(response.topInfluentialFeatures()).hasSize(2);
            assertThat(response.topInfluentialFeatures().get(0).key()).isEqualTo("diff_clean_freq");
            assertThat(response.topInfluentialFeatures().get(0).label()).isEqualTo("청소 빈도");
            assertThat(response.topInfluentialFeatures().get(1).label()).isEqualTo("흡연 습관");
            assertThat(response.summaryComment().brief()).isEqualTo("브리프");
            assertThat(response.summaryComment().caution()).isEqualTo("코션");
        }

        @Test
        @DisplayName("topInfluentialFeatures가 비어있어도 빈 리스트로 응답한다")
        void topInfluential이_비어있는_케이스() {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            MatchAnalysisResult result = new MatchAnalysisResult(
                    60,
                    MatchCounts.of(4, 4, 8),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    SummaryComment.of("", "", "")
            );

            given(matchReportDataLoader.loadCommand(requesterId, targetUserId)).willReturn(command);
            given(matchReportPort.analyze(command)).willReturn(result);
            given(featureLabelConverter.toLabels(List.of())).willReturn(List.of());

            // when
            MatchRateResponse response = matchReportUseCase.analyzeMatchReport(requesterId, targetUserId);

            // then
            assertThat(response.matchedFeatures()).isEmpty();
            assertThat(response.mismatchedFeatures()).isEmpty();
            assertThat(response.conversationStarters()).isEmpty();
            assertThat(response.topInfluentialFeatures()).isEmpty();
        }

        @Test
        @DisplayName("로더에서 발생한 예외는 그대로 전파된다")
        void 로더_예외_전파() {
            // given
            Long requesterId = 1L;
            Long targetUserId = 1L;
            given(matchReportDataLoader.loadCommand(requesterId, targetUserId))
                    .willThrow(SelfMatchNotAllowedException.class);

            // when & then
            assertThatThrownBy(() -> matchReportUseCase.analyzeMatchReport(requesterId, targetUserId))
                    .isInstanceOf(SelfMatchNotAllowedException.class);
        }

        @Test
        @DisplayName("AI API 호출 실패 시 AiServiceUnavailableException이 전파된다")
        void AI_API_실패_시_예외_전파() {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            MatchAnalysisCommand command = MatchAnalysisCommand.of(null, null);

            given(matchReportDataLoader.loadCommand(requesterId, targetUserId)).willReturn(command);
            given(matchReportPort.analyze(any(MatchAnalysisCommand.class)))
                    .willThrow(AiServiceUnavailableException.class);

            // when & then
            assertThatThrownBy(() -> matchReportUseCase.analyzeMatchReport(requesterId, targetUserId))
                    .isInstanceOf(AiServiceUnavailableException.class);
        }
    }
}
