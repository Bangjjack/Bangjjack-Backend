package com.project.bangjjack.domain.chat.application;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessagePageResponse;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomNotFoundException;
import com.project.bangjjack.domain.chat.application.exception.NotChatParticipantException;
import com.project.bangjjack.domain.chat.application.usecase.ChatMessageUseCase;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;
import com.project.bangjjack.domain.chat.domain.service.ChatMessageGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatSaveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ChatMessageUseCaseGetMessagesTest {

    @Mock
    private ChatRoomGetService chatRoomGetService;

    @Mock
    private ChatSaveService chatSaveService; // ChatMessageUseCase 의존성 주입에 필요

    @Mock
    private ChatMessageGetService chatMessageGetService;

    @Mock
    private ApplicationEventPublisher eventPublisher; // ChatMessageUseCase 의존성 주입에 필요

    @InjectMocks
    private ChatMessageUseCase chatMessageUseCase;

    private final AtomicLong idSequence = new AtomicLong(1L);

    private Chat createChat() {
        ChatRoom chatRoom = ChatRoom.createDirect(1L, "DM_1_2");
        Chat chat = Chat.create(1L, chatRoom, "테스트 메시지", MessageType.USER);
        ReflectionTestUtils.setField(chat, "id", idSequence.getAndIncrement());
        return chat;
    }

    private ChatRoomParticipant createParticipant(Long userId) {
        ChatRoom chatRoom = ChatRoom.createDirect(1L, "DM_1_2");
        return ChatRoomParticipant.create(chatRoom, userId);
    }

    @Nested
    @DisplayName("채팅 메시지 조회 시")
    class GetMessages {

        @Test
        @DisplayName("참여자가 조회하면 메시지 목록을 반환한다")
        void 참여자가_조회하면_메시지_목록을_반환한다() {
            // given
            Long currentUserId = 1L;
            Long roomId = 10L;
            int size = 30;
            List<Chat> chats = List.of(createChat());

            given(chatRoomGetService.getParticipant(roomId, currentUserId)).willReturn(createParticipant(currentUserId));
            given(chatMessageGetService.getMessages(roomId, null, size)).willReturn(chats);

            // when
            ChatMessagePageResponse response = chatMessageUseCase.getMessages(currentUserId, roomId, null, size);

            // then
            assertThat(response.messages()).hasSize(1);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
            then(chatRoomGetService).should().getParticipant(roomId, currentUserId);
        }

        @Test
        @DisplayName("size보다 많은 메시지가 있으면 hasNext=true이고 nextCursor가 반환된다")
        void size보다_많으면_hasNext가_true이다() {
            // given
            Long roomId = 10L;
            int size = 2;
            // size+1개 반환 (도메인 서비스가 size+1 조회 → UseCase가 잘라냄)
            List<Chat> fetched = List.of(createChat(), createChat(), createChat());

            given(chatRoomGetService.getParticipant(roomId, 1L)).willReturn(createParticipant(1L));
            given(chatMessageGetService.getMessages(roomId, null, size)).willReturn(fetched);

            // when
            ChatMessagePageResponse response = chatMessageUseCase.getMessages(1L, roomId, null, size);

            // then
            assertThat(response.messages()).hasSize(size);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isNotNull();
        }

        @Test
        @DisplayName("cursor와 함께 조회하면 nextCursor와 hasNext를 정확하게 계산한다")
        void cursor와_함께_조회하면_페이지네이션_정보를_반환한다() {
            // given
            Long roomId = 10L;
            Long cursor = 50L;
            int size = 2;
            List<Chat> fetched = List.of(createChat(), createChat());

            given(chatRoomGetService.getParticipant(roomId, 1L)).willReturn(createParticipant(1L));
            given(chatMessageGetService.getMessages(roomId, cursor, size)).willReturn(fetched);

            // when
            ChatMessagePageResponse response = chatMessageUseCase.getMessages(1L, roomId, cursor, size);

            // then
            assertThat(response.messages()).hasSize(2);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("메시지가 없으면 빈 리스트와 hasNext=false를 반환한다")
        void 메시지가_없으면_빈_리스트를_반환한다() {
            // given
            Long roomId = 10L;
            given(chatRoomGetService.getParticipant(roomId, 1L)).willReturn(createParticipant(1L));
            given(chatMessageGetService.getMessages(roomId, null, 30)).willReturn(Collections.emptyList());

            // when
            ChatMessagePageResponse response = chatMessageUseCase.getMessages(1L, roomId, null, 30);

            // then
            assertThat(response.messages()).isEmpty();
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 채팅방이면 ChatRoomNotFoundException이 발생한다")
        void 존재하지_않는_채팅방이면_예외가_발생한다() {
            // given
            Long currentUserId = 1L;
            Long roomId = 999L;

            doThrow(new ChatRoomNotFoundException())
                    .when(chatRoomGetService).getParticipant(roomId, currentUserId);

            // when & then
            assertThatThrownBy(() -> chatMessageUseCase.getMessages(currentUserId, roomId, null, 30))
                    .isInstanceOf(ChatRoomNotFoundException.class);
        }

        @Test
        @DisplayName("채팅방 비참여자가 조회하면 NotChatParticipantException이 발생한다")
        void 비참여자가_조회하면_예외가_발생한다() {
            // given
            Long outsiderId = 99L;
            Long roomId = 10L;

            doThrow(new NotChatParticipantException())
                    .when(chatRoomGetService).getParticipant(roomId, outsiderId);

            // when & then
            assertThatThrownBy(() -> chatMessageUseCase.getMessages(outsiderId, roomId, null, 30))
                    .isInstanceOf(NotChatParticipantException.class);
        }
    }
}
