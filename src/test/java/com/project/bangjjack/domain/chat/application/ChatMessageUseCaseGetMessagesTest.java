package com.project.bangjjack.domain.chat.application;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessagePageResponse;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageResponse;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomNotFoundException;
import com.project.bangjjack.domain.chat.application.exception.NotChatParticipantException;
import com.project.bangjjack.domain.chat.application.usecase.ChatMessageUseCase;
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

import java.time.LocalDateTime;
import java.util.List;

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
    private ChatSaveService chatSaveService;

    @Mock
    private ChatMessageGetService chatMessageGetService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ChatMessageUseCase chatMessageUseCase;

    @Nested
    @DisplayName("채팅 메시지 조회 시")
    class GetMessages {

        @Test
        @DisplayName("참여자가 조회하면 메시지 목록을 반환한다")
        void 참여자가_조회하면_메시지_목록을_반환한다() {
            // given
            Long currentUserId = 1L;
            Long roomId = 10L;
            Long cursor = null;
            int size = 30;

            ChatMessagePageResponse expected = new ChatMessagePageResponse(
                    List.of(new ChatMessageResponse(5L, currentUserId, "안녕", LocalDateTime.now())),
                    null,
                    false
            );

            given(chatMessageGetService.getMessages(roomId, cursor, size)).willReturn(expected);

            // when
            ChatMessagePageResponse response = chatMessageUseCase.getMessages(currentUserId, roomId, cursor, size);

            // then
            assertThat(response.messages()).hasSize(1);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
            then(chatRoomGetService).should().getByIdAndValidateParticipant(roomId, currentUserId);
        }

        @Test
        @DisplayName("cursor와 함께 조회하면 해당 커서 이후 메시지와 다음 페이지 정보를 반환한다")
        void cursor와_함께_조회하면_페이지네이션_정보를_반환한다() {
            // given
            Long currentUserId = 1L;
            Long roomId = 10L;
            Long cursor = 50L;
            int size = 2;

            ChatMessagePageResponse expected = new ChatMessagePageResponse(
                    List.of(
                            new ChatMessageResponse(49L, currentUserId, "메시지1", LocalDateTime.now()),
                            new ChatMessageResponse(48L, currentUserId, "메시지2", LocalDateTime.now())
                    ),
                    48L,
                    true
            );

            given(chatMessageGetService.getMessages(roomId, cursor, size)).willReturn(expected);

            // when
            ChatMessagePageResponse response = chatMessageUseCase.getMessages(currentUserId, roomId, cursor, size);

            // then
            assertThat(response.messages()).hasSize(2);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo(48L);
        }

        @Test
        @DisplayName("존재하지 않는 채팅방이면 ChatRoomNotFoundException이 발생한다")
        void 존재하지_않는_채팅방이면_예외가_발생한다() {
            // given
            Long currentUserId = 1L;
            Long roomId = 999L;

            doThrow(new ChatRoomNotFoundException())
                    .when(chatRoomGetService).getByIdAndValidateParticipant(roomId, currentUserId);

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
                    .when(chatRoomGetService).getByIdAndValidateParticipant(roomId, outsiderId);

            // when & then
            assertThatThrownBy(() -> chatMessageUseCase.getMessages(outsiderId, roomId, null, 30))
                    .isInstanceOf(NotChatParticipantException.class);
        }
    }
}
