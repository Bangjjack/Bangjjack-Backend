package com.project.bangjjack.domain.chat.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatRoomParticipant")
class ChatRoomParticipantTest {

    private ChatRoomParticipant participant() {
        ChatRoom room = ChatRoom.createDirect(1L, "DM_1_2");
        return ChatRoomParticipant.create(room, 1L);
    }

    @Nested
    @DisplayName("최초 읽음 처리")
    class FirstRead {

        @Test
        @DisplayName("lastReadMessageId가 null이면 주어진 ID로 저장되고 unreadCount가 0이 된다")
        void null_상태에서_저장() {
            ChatRoomParticipant p = participant();

            p.markAsRead(100L);

            assertThat(p.getLastReadMessageId()).isEqualTo(100L);
            assertThat(p.getUnreadCount()).isZero();
        }
    }

    @Nested
    @DisplayName("더 최신 메시지 읽음 처리")
    class NewerMessageRead {

        @Test
        @DisplayName("새 ID가 더 크면 lastReadMessageId가 갱신된다")
        void 더_큰_ID면_갱신() {
            ChatRoomParticipant p = participant();
            p.markAsRead(100L);

            p.markAsRead(200L);

            assertThat(p.getLastReadMessageId()).isEqualTo(200L);
        }
    }

    @Nested
    @DisplayName("이전 페이지(더 오래된 메시지) 읽음 처리")
    class OlderPageRead {

        @Test
        @DisplayName("새 ID가 더 작으면 lastReadMessageId가 후퇴하지 않는다")
        void 더_작은_ID면_유지() {
            ChatRoomParticipant p = participant();
            p.markAsRead(200L);

            p.markAsRead(100L);

            assertThat(p.getLastReadMessageId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("이전 페이지를 읽어도 unreadCount는 0으로 유지된다")
        void 이전_페이지_읽어도_unreadCount는_0() {
            ChatRoomParticipant p = participant();
            p.markAsRead(200L);

            p.markAsRead(100L);

            assertThat(p.getUnreadCount()).isZero();
        }
    }

    @Nested
    @DisplayName("rejoin")
    class Rejoin {

        @Test
        @DisplayName("나간 참여자가 재참여하면 status가 ACTIVE가 된다")
        void 재참여_후_ACTIVE() {
            ChatRoomParticipant p = participant();
            p.leave(100L);

            p.rejoin();

            assertThat(p.getStatus()).isEqualTo(ParticipantStatus.ACTIVE);
        }

        @Test
        @DisplayName("재참여 후에도 visibleFromMessageId는 유지된다")
        void 재참여_후_visibleFromMessageId_유지() {
            ChatRoomParticipant p = participant();
            p.leave(100L);

            p.rejoin();

            assertThat(p.getVisibleFromMessageId()).isEqualTo(100L);
        }
    }
}
