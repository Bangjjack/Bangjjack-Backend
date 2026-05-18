package com.project.bangjjack.domain.post.domain.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FeatureLabelConverter {

    private static final Map<String, String> FEATURE_LABELS = Map.ofEntries(
            Map.entry("diff_sleep_time", "취침 시간"),
            Map.entry("diff_wake_time", "기상 시간"),
            Map.entry("diff_night_call", "밤 통화 빈도"),
            Map.entry("diff_alarm_habit", "알람 습관"),
            Map.entry("diff_clean_freq", "청소 빈도"),
            Map.entry("diff_eating_in_room", "방 내 취식 허용도"),
            Map.entry("diff_time_at_home", "기숙사 체류 시간"),
            Map.entry("diff_noise_sensitivity", "소음 민감도"),
            Map.entry("diff_privacy_importance", "프라이버시 중요도"),
            Map.entry("diff_tolerance", "생활패턴 허용도"),
            Map.entry("match_gender", "성별"),
            Map.entry("match_smoking", "흡연 습관"),
            Map.entry("match_habit_toss", "잠버릇 (뒤척임)"),
            Map.entry("match_habit_wakeup", "잠버릇 (자다 깸)"),
            Map.entry("match_habit_snore", "잠버릇 (코골이)"),
            Map.entry("match_habit_talk", "잠버릇 (잠꼬대)"),
            Map.entry("match_habit_grind", "잠버릇 (이갈이)"),
            Map.entry("match_dorm_exp_none", "기숙사 경험 (없음)"),
            Map.entry("match_dorm_exp_double", "기숙사 경험 (2인실)"),
            Map.entry("match_dorm_exp_triple", "기숙사 경험 (3인실 이상)"),
            Map.entry("match_prio1_sleep", "1순위: 수면 패턴"),
            Map.entry("match_prio1_clean", "1순위: 청결도"),
            Map.entry("match_prio1_noise", "1순위: 소음"),
            Map.entry("match_prio1_smoking", "1순위: 흡연 여부"),
            Map.entry("match_prio1_rhythm", "1순위: 생활 리듬"),
            Map.entry("match_prio2_sleep", "2순위: 수면 패턴"),
            Map.entry("match_prio2_clean", "2순위: 청결도"),
            Map.entry("match_prio2_noise", "2순위: 소음"),
            Map.entry("match_prio2_smoking", "2순위: 흡연 여부"),
            Map.entry("match_prio2_rhythm", "2순위: 생활 리듬")
    );

    public List<String> toLabels(List<String> featureKeys) {
        if (featureKeys == null) {
            return List.of();
        }
        return featureKeys.stream()
                .map(key -> FEATURE_LABELS.getOrDefault(key, key))
                .toList();
    }
}
