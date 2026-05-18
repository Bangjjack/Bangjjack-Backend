package com.project.bangjjack.domain.post.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FeatureLabelConverter")
class FeatureLabelConverterTest {

    private final FeatureLabelConverter converter = new FeatureLabelConverter();

    @Nested
    @DisplayName("toLabels")
    class ToLabels {

        @Test
        @DisplayName("매핑된 피처 키 리스트가 주어지면 한국어 라벨 리스트를 반환한다")
        void 매핑된_키는_한국어_라벨로_변환된다() {
            // given
            List<String> keys = List.of("diff_sleep_time", "match_smoking", "match_prio1_clean");

            // when
            List<String> labels = converter.toLabels(keys);

            // then
            assertThat(labels).containsExactly("취침 시간", "흡연 습관", "1순위: 청결도");
        }

        @Test
        @DisplayName("매핑에 없는 키는 키를 그대로 반환한다 (서버 무중단 원칙)")
        void 매핑되지_않은_키는_그대로_반환된다() {
            // given
            List<String> keys = List.of("unknown_feature", "diff_sleep_time");

            // when
            List<String> labels = converter.toLabels(keys);

            // then
            assertThat(labels).containsExactly("unknown_feature", "취침 시간");
        }

        @Test
        @DisplayName("빈 리스트가 주어지면 빈 리스트를 반환한다")
        void 빈_리스트는_빈_리스트를_반환한다() {
            // when
            List<String> labels = converter.toLabels(List.of());

            // then
            assertThat(labels).isEmpty();
        }

        @Test
        @DisplayName("null이 주어지면 빈 리스트를 반환한다")
        void null은_빈_리스트를_반환한다() {
            // when
            List<String> labels = converter.toLabels(null);

            // then
            assertThat(labels).isEmpty();
        }
    }
}
