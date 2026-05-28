package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AiMatchResponse JSON 역직렬화")
class AiMatchResponseDeserializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("AI 서버 실제 응답(matchedFeatures 객체 배열, summaryComment 등 포함)을 역직렬화한다 — 회귀 방지: 과거에 List<String>으로 선언돼 MismatchedInputException 발생함")
    void 실제_AI_응답_스키마를_역직렬화한다() throws Exception {
        // given
        String json = """
                {
                  "matchRate": 82,
                  "counts": { "matched": 6, "mismatched": 2, "total": 8 },
                  "matchedFeatures": [
                    { "key": "bedtime", "label": "취침 시간", "description": "둘 다 24~2시로 자는 편이에요" },
                    { "key": "smoking", "label": "흡연", "description": "둘 다 비흡연자예요" }
                  ],
                  "mismatchedFeatures": [
                    {
                      "key": "clean_freq",
                      "label": "청소 주기",
                      "description": "청소 주기가 달라요 (가끔 ↔ 거의 매일)",
                      "advice": "청소 담당과 청소 방식에 대해 미리 이야기 나눠보세요"
                    }
                  ],
                  "conversationStarters": [
                    {
                      "key": "diff_clean_freq",
                      "starter": "\\"청소 보통 얼마나 자주 해?\\"",
                      "subtitle": "청소 습관과 분담을 미리 맞춰봐요"
                    }
                  ],
                  "topInfluentialFeatures": ["diff_clean_freq", "match_smoking", "diff_sleep_time"],
                  "summaryComment": {
                    "brief": "비슷한 점이 많지만 청소 주기는 미리 조율이 필요해요.",
                    "positive": "취침 시간과 흡연 항목에서 높은 일치율을 보였어요.",
                    "caution": "다만 청소 주기 항목에서 차이가 있어요."
                  }
                }
                """;

        // when
        AiMatchResponse response = objectMapper.readValue(json, AiMatchResponse.class);

        // then
        assertThat(response.matchRate()).isEqualTo(82);
        assertThat(response.counts()).isEqualTo(new AiMatchCounts(6, 2, 8));
        assertThat(response.matchedFeatures()).hasSize(2);
        assertThat(response.matchedFeatures().get(0).key()).isEqualTo("bedtime");
        assertThat(response.matchedFeatures().get(0).label()).isEqualTo("취침 시간");
        assertThat(response.matchedFeatures().get(0).description()).isEqualTo("둘 다 24~2시로 자는 편이에요");
        assertThat(response.mismatchedFeatures()).hasSize(1);
        assertThat(response.mismatchedFeatures().get(0).advice()).isEqualTo("청소 담당과 청소 방식에 대해 미리 이야기 나눠보세요");
        assertThat(response.conversationStarters()).hasSize(1);
        assertThat(response.conversationStarters().get(0).starter()).contains("청소 보통 얼마나");
        assertThat(response.topInfluentialFeatures()).containsExactly("diff_clean_freq", "match_smoking", "diff_sleep_time");
        assertThat(response.summaryComment().brief()).contains("미리 조율");
        assertThat(response.summaryComment().caution()).contains("차이가 있어요");
    }

    @Test
    @DisplayName("matchedFeatures가 빈 배열이고 caution이 빈 문자열인 응답도 정상 역직렬화한다")
    void 빈_배열과_빈_문자열_케이스() throws Exception {
        // given
        String json = """
                {
                  "matchRate": 35,
                  "counts": { "matched": 0, "mismatched": 8, "total": 8 },
                  "matchedFeatures": [],
                  "mismatchedFeatures": [],
                  "conversationStarters": [],
                  "topInfluentialFeatures": [],
                  "summaryComment": { "brief": "차이가 많아요.", "positive": "", "caution": "" }
                }
                """;

        // when
        AiMatchResponse response = objectMapper.readValue(json, AiMatchResponse.class);

        // then
        assertThat(response.matchRate()).isEqualTo(35);
        assertThat(response.matchedFeatures()).isEmpty();
        assertThat(response.summaryComment().positive()).isEmpty();
        assertThat(response.summaryComment().caution()).isEmpty();
    }

    @Test
    @DisplayName("스펙에 명시되지 않은 추가 필드가 있어도 무시하고 역직렬화한다")
    void unknown_필드_무시() throws Exception {
        // given
        String json = """
                {
                  "matchRate": 70,
                  "counts": { "matched": 4, "mismatched": 4, "total": 8 },
                  "matchedFeatures": [],
                  "mismatchedFeatures": [],
                  "conversationStarters": [],
                  "topInfluentialFeatures": [],
                  "summaryComment": { "brief": "x", "positive": "y", "caution": "z" },
                  "experimental_field": { "foo": "bar" }
                }
                """;

        // when
        AiMatchResponse response = objectMapper.readValue(json, AiMatchResponse.class);

        // then
        assertThat(response.matchRate()).isEqualTo(70);
    }
}
