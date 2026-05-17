package com.project.bangjjack.domain.post.infrastructure.aimatch.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AiMatchRequest JSON 직렬화")
class AiMatchRequestSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("AiMatchRequest는 snake_case 필드명(user_a, user_b)으로 직렬화된다")
    void user_a와_user_b로_직렬화된다() throws Exception {
        // given
        AiMatchUserPayload payload = samplePayload();
        AiMatchRequest request = AiMatchRequest.of(payload, payload);

        // when
        String json = objectMapper.writeValueAsString(request);

        // then
        assertThat(json).contains("\"user_a\":");
        assertThat(json).contains("\"user_b\":");
        assertThat(json).doesNotContain("\"userA\"");
        assertThat(json).doesNotContain("\"userB\"");
    }

    @Test
    @DisplayName("AiMatchUserPayload의 모든 다중 단어 필드는 snake_case로 직렬화된다")
    void payload_필드는_snake_case로_직렬화된다() throws Exception {
        // given
        AiMatchUserPayload payload = samplePayload();

        // when
        String json = objectMapper.writeValueAsString(payload);

        // then
        assertThat(json)
                .contains("\"wake_up_time\":")
                .contains("\"call_habit\":")
                .contains("\"cleaning_cycle\":")
                .contains("\"dorm_stay_time\":")
                .contains("\"noise_sensitivity\":")
                .contains("\"sleep_habits\":")
                .contains("\"first_priority\":")
                .contains("\"second_priority\":")
                .contains("\"third_priority\":");
        assertThat(json)
                .doesNotContain("\"wakeUpTime\"")
                .doesNotContain("\"callHabit\"")
                .doesNotContain("\"sleepHabits\"")
                .doesNotContain("\"firstPriority\"");
    }

    @Test
    @DisplayName("Enum 값은 name() 그대로 직렬화된다 (DB ENUM 값 그대로 전달)")
    void enum_값은_그대로_직렬화된다() throws Exception {
        // given
        AiMatchUserPayload payload = samplePayload();

        // when
        String json = objectMapper.writeValueAsString(payload);

        // then
        assertThat(json)
                .contains("\"gender\":\"MALE\"")
                .contains("\"bedtime\":\"BETWEEN_22_24\"")
                .contains("\"wake_up_time\":\"BETWEEN_6_8\"")
                .contains("\"sleep_habits\":[\"TOSS_AND_TURN\",\"SNORING\"]");
    }

    private AiMatchUserPayload samplePayload() {
        return new AiMatchUserPayload(
                Gender.MALE,
                Bedtime.BETWEEN_22_24,
                WakeUpTime.BETWEEN_6_8,
                CallHabit.WHISPER,
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.HALF_AND_HALF,
                NoiseSensitivity.NORMAL,
                Smoking.NON_SMOKER,
                List.of(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING),
                RoommatePreferenceFactor.BEDTIME,
                RoommatePreferenceFactor.CLEANING_HABIT,
                RoommatePreferenceFactor.NOISE_SENSITIVITY
        );
    }
}
