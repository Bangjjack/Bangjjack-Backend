package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.roommategroup.application.event.RoommateGroupDisbandedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoommateGroupDisbandedChatListener")
class RoommateGroupDisbandedChatListenerTest {

    @Mock
    private RoommateGroupDisbandedNotifier notifier;

    @InjectMocks
    private RoommateGroupDisbandedChatListener listener;

    private static final Long LEADER_ID = 1L;
    private static final String POST_TITLE = "기숙사 룸메 구해요";
    private static final String EXPECTED_CONTENT = "'기숙사 룸메 구해요' 룸메이트 그룹이 해체되어 모집글이 삭제되었습니다.";

    @Test
    @DisplayName("멤버 각각에 대해 notifier로 DM 시스템 메시지 전송을 위임한다")
    void 멤버별_notifier_위임() {
        // given
        List<Long> memberIds = List.of(2L, 3L, 4L);

        // when
        listener.handle(new RoommateGroupDisbandedEvent(LEADER_ID, memberIds, POST_TITLE));

        // then
        for (Long memberId : memberIds) {
            then(notifier).should().sendDirectSystemMessage(LEADER_ID, memberId, EXPECTED_CONTENT);
        }
        then(notifier).should(times(3)).sendDirectSystemMessage(eq(LEADER_ID), anyLong(), eq(EXPECTED_CONTENT));
    }

    @Test
    @DisplayName("한 멤버 전송에서 예외가 발생해도 나머지 멤버에게는 전송을 계속한다")
    void 일부_실패해도_나머지_전송() {
        // given
        Long failingMemberId = 2L;
        Long successMemberId = 3L;
        willThrow(new RuntimeException("DB error"))
                .given(notifier).sendDirectSystemMessage(LEADER_ID, failingMemberId, EXPECTED_CONTENT);

        // when
        listener.handle(new RoommateGroupDisbandedEvent(
                LEADER_ID, List.of(failingMemberId, successMemberId), POST_TITLE));

        // then
        then(notifier).should().sendDirectSystemMessage(LEADER_ID, failingMemberId, EXPECTED_CONTENT);
        then(notifier).should().sendDirectSystemMessage(LEADER_ID, successMemberId, EXPECTED_CONTENT);
    }

    @Test
    @DisplayName("메시지 본문에 모집글 제목이 포함된 형식으로 전달된다")
    void 본문_제목_포함() {
        // given
        Long memberId = 2L;
        String customTitle = "특별한 룸메 모집";
        String expectedContent = "'특별한 룸메 모집' 룸메이트 그룹이 해체되어 모집글이 삭제되었습니다.";

        // when
        listener.handle(new RoommateGroupDisbandedEvent(LEADER_ID, List.of(memberId), customTitle));

        // then
        then(notifier).should().sendDirectSystemMessage(LEADER_ID, memberId, expectedContent);
        then(notifier).should().sendDirectSystemMessage(eq(LEADER_ID), eq(memberId), anyString());
    }
}
