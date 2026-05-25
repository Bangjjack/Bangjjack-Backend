package com.project.bangjjack.domain.chat.application;

import com.project.bangjjack.domain.chat.application.exception.AlreadyLeftChatRoomException;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomNotFoundException;
import com.project.bangjjack.domain.chat.application.exception.NotChatParticipantException;
import com.project.bangjjack.domain.chat.application.usecase.ChatRoomUseCase;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.ParticipantStatus;
import com.project.bangjjack.domain.chat.domain.entity.RoomStatus;
import com.project.bangjjack.domain.chat.domain.service.ChatMessageGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatRoomUseCaseLeaveChatRoomTest {

    @Mock private ChatRoomCreateService chatRoomCreateService;
    @Mock private ChatRoomGetService chatRoomGetService;
    @Mock private ChatMessageGetService chatMessageGetService;
    @Mock private UserGetService userGetService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChatRoomUseCase chatRoomUseCase;

    @Nested
    @DisplayName("채팅방 나가기 시")
    class LeaveChatRoom {

        @Test
        @DisplayName("파트너가 남아 있으면 participant.status=LEFT가 되고 채팅방은 OPEN 유지된다")
        void 파트너가_남아_있으면_방은_OPEN_유지된다() {
            // given
            Long roomId = 1L;
            Long userId = 10L;
            Long partnerId = 20L;

            ChatRoom chatRoom = ChatRoom.createDirect(userId, "DM_10_20");
            ChatRoomParticipant myParticipant = ChatRoomParticipant.create(chatRoom, userId);
            ChatRoomParticipant partnerParticipant = ChatRoomParticipant.create(chatRoom, partnerId);

            given(chatRoomGetService.getActiveParticipant(roomId, userId)).willReturn(myParticipant);

            // when
            chatRoomUseCase.leaveChatRoom(roomId, userId);

            // then
            assertThat(myParticipant.getStatus()).isEqualTo(ParticipantStatus.LEFT);
            assertThat(chatRoom.getStatus()).isEqualTo(RoomStatus.OPEN);
        }

        @Test
        @DisplayName("마지막 참여자가 나가도 채팅방은 OPEN 유지된다")
        void 마지막_참여자가_나가도_채팅방은_OPEN_유지된다() {
            // given
            Long roomId = 1L;
            Long userId = 10L;

            ChatRoom chatRoom = ChatRoom.createDirect(userId, "DM_10_20");
            ChatRoomParticipant myParticipant = ChatRoomParticipant.create(chatRoom, userId);

            given(chatRoomGetService.getActiveParticipant(roomId, userId)).willReturn(myParticipant);

            // when
            chatRoomUseCase.leaveChatRoom(roomId, userId);

            // then
            assertThat(myParticipant.getStatus()).isEqualTo(ParticipantStatus.LEFT);
            assertThat(chatRoom.getStatus()).isEqualTo(RoomStatus.OPEN);
        }

        @Test
        @DisplayName("존재하지 않는 채팅방 ID면 ChatRoomNotFoundException이 발생한다")
        void 존재하지_않는_채팅방이면_예외_발생() {
            // given
            Long roomId = 999L;
            Long userId = 10L;

            given(chatRoomGetService.getActiveParticipant(roomId, userId))
                    .willThrow(new ChatRoomNotFoundException());

            // when & then
            assertThatThrownBy(() -> chatRoomUseCase.leaveChatRoom(roomId, userId))
                    .isInstanceOf(ChatRoomNotFoundException.class);
        }

        @Test
        @DisplayName("이미 나간 참여자가 재요청하면 AlreadyLeftChatRoomException이 발생한다")
        void 이미_나간_참여자가_재요청하면_예외_발생() {
            // given
            Long roomId = 1L;
            Long userId = 10L;

            given(chatRoomGetService.getActiveParticipant(roomId, userId))
                    .willThrow(new AlreadyLeftChatRoomException());

            // when & then
            assertThatThrownBy(() -> chatRoomUseCase.leaveChatRoom(roomId, userId))
                    .isInstanceOf(AlreadyLeftChatRoomException.class);
        }
    }
}
