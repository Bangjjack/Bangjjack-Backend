package com.project.bangjjack.domain.chat.application;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.event.ChatMessageSavedEvent;
import com.project.bangjjack.domain.chat.application.exception.ChatForbiddenException;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomClosedException;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomNotFoundException;
import com.project.bangjjack.domain.chat.application.usecase.SendChatMessageUseCase;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.service.ChatCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendChatMessageUseCaseTest {

    @Mock
    private ChatRoomGetService chatRoomGetService;

    @Mock
    private ChatCreateService chatCreateService;

    @Mock
    private UserGetService userGetService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SendChatMessageUseCase sendChatMessageUseCase;

    @Nested
    @DisplayName("채팅 메시지 전송 시")
    class SendMessage {

        @Test
        @DisplayName("참여자가 메시지를 전송하면 Chat이 저장되고 ChatMessageSavedEvent가 발행된다")
        void 참여자_메시지_전송_시_저장_및_이벤트_발행() {
            // given
            Long roomId = 1L;
            Long senderId = 10L;
            SendChatMessageRequest request = new SendChatMessageRequest("안녕하세요");

            ChatRoom chatRoom = ChatRoom.createDirect(senderId, "DM_10_20");
            User sender = User.create("provider-id", "홍길동", "hong@test.com", "https://profile.jpg");
            Chat savedChat = Chat.create(senderId, chatRoom, "안녕하세요");

            given(chatRoomGetService.getById(roomId)).willReturn(chatRoom);
            given(chatRoomGetService.isParticipant(roomId, senderId)).willReturn(true);
            given(userGetService.getById(senderId)).willReturn(sender);
            given(chatCreateService.save(any(Chat.class))).willReturn(savedChat);

            // when
            sendChatMessageUseCase.execute(roomId, senderId, request);

            // then
            verify(chatCreateService).save(any(Chat.class));
            ArgumentCaptor<ChatMessageSavedEvent> captor = ArgumentCaptor.forClass(ChatMessageSavedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());

            ChatMessageSavedEvent event = captor.getValue();
            assertThat(event.roomId()).isEqualTo(roomId);
            assertThat(event.senderId()).isEqualTo(senderId);
            assertThat(event.senderNickname()).isEqualTo("홍길동");
            assertThat(event.content()).isEqualTo("안녕하세요");
        }

        @Test
        @DisplayName("존재하지 않는 채팅방에 메시지를 전송하면 ChatRoomNotFoundException이 발생한다")
        void 존재하지_않는_채팅방_전송_시_예외_발생() {
            // given
            Long roomId = 999L;
            Long senderId = 10L;
            SendChatMessageRequest request = new SendChatMessageRequest("안녕하세요");

            given(chatRoomGetService.getById(roomId)).willThrow(new ChatRoomNotFoundException());

            // when & then
            assertThatThrownBy(() -> sendChatMessageUseCase.execute(roomId, senderId, request))
                    .isInstanceOf(ChatRoomNotFoundException.class);
        }

        @Test
        @DisplayName("CLOSED 상태 채팅방에 메시지를 전송하면 ChatRoomClosedException이 발생한다")
        void 종료된_채팅방_전송_시_예외_발생() {
            // given
            Long roomId = 1L;
            Long senderId = 10L;
            SendChatMessageRequest request = new SendChatMessageRequest("안녕하세요");

            ChatRoom chatRoom = ChatRoom.createDirect(senderId, "DM_10_20");
            chatRoom.close();

            given(chatRoomGetService.getById(roomId)).willReturn(chatRoom);

            // when & then
            assertThatThrownBy(() -> sendChatMessageUseCase.execute(roomId, senderId, request))
                    .isInstanceOf(ChatRoomClosedException.class);
        }

        @Test
        @DisplayName("채팅방 비참여자가 메시지를 전송하면 ChatForbiddenException이 발생한다")
        void 비참여자_메시지_전송_시_예외_발생() {
            // given
            Long roomId = 1L;
            Long outsiderId = 99L;
            SendChatMessageRequest request = new SendChatMessageRequest("안녕하세요");

            ChatRoom chatRoom = ChatRoom.createDirect(10L, "DM_10_20");

            given(chatRoomGetService.getById(roomId)).willReturn(chatRoom);
            given(chatRoomGetService.isParticipant(roomId, outsiderId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> sendChatMessageUseCase.execute(roomId, outsiderId, request))
                    .isInstanceOf(ChatForbiddenException.class);
        }
    }
}
