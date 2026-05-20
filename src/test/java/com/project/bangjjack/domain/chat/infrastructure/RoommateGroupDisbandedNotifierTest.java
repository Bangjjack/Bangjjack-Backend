package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.chat.application.event.ChatMessageSentEvent;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatSaveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoommateGroupDisbandedNotifier")
class RoommateGroupDisbandedNotifierTest {

    @Mock
    private ChatRoomGetService chatRoomGetService;

    @Mock
    private ChatRoomCreateService chatRoomCreateService;

    @Mock
    private ChatSaveService chatSaveService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RoommateGroupDisbandedNotifier notifier;

    private static final Long SENDER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final String CONTENT = "공지 메시지";

    private ChatRoom mockChatRoom(Long roomId) {
        ChatRoom chatRoom = mock(ChatRoom.class);
        lenient().when(chatRoom.getId()).thenReturn(roomId);
        return chatRoom;
    }

    private Chat mockSavedChat(Long messageId) {
        Chat chat = mock(Chat.class);
        lenient().when(chat.getId()).thenReturn(messageId);
        lenient().when(chat.getSenderId()).thenReturn(SENDER_ID);
        lenient().when(chat.getContent()).thenReturn(CONTENT);
        return chat;
    }

    @Test
    @DisplayName("기존 DM 방이 있으면 재사용하여 메시지를 저장하고 브로드캐스트 이벤트를 발행한다")
    void 기존_DM_재사용() {
        // given
        String directKey = ChatRoom.generateDirectKey(SENDER_ID, RECEIVER_ID);
        ChatRoom existing = mockChatRoom(100L);
        Chat saved = mockSavedChat(900L);
        given(chatRoomGetService.findByDirectRoomKey(directKey)).willReturn(Optional.of(existing));
        given(chatSaveService.save(SENDER_ID, existing, CONTENT)).willReturn(saved);

        // when
        notifier.sendDirectSystemMessage(SENDER_ID, RECEIVER_ID, CONTENT);

        // then
        then(chatRoomCreateService).should(never()).createDirectRoom(anyLong(), anyLong(), anyString());
        then(chatSaveService).should().save(SENDER_ID, existing, CONTENT);

        ArgumentCaptor<ChatMessageSentEvent> captor = ArgumentCaptor.forClass(ChatMessageSentEvent.class);
        then(eventPublisher).should().publishEvent(captor.capture());
        ChatMessageSentEvent published = captor.getValue();
        assertThat(published.roomId()).isEqualTo(100L);
        assertThat(published.broadcast().senderId()).isEqualTo(SENDER_ID);
        assertThat(published.broadcast().content()).isEqualTo(CONTENT);
    }

    @Test
    @DisplayName("DM 방이 없으면 신규 생성하여 메시지를 저장한다")
    void DM_신규_생성() {
        // given
        String directKey = ChatRoom.generateDirectKey(SENDER_ID, RECEIVER_ID);
        ChatRoom created = mockChatRoom(101L);
        Chat saved = mockSavedChat(901L);
        given(chatRoomGetService.findByDirectRoomKey(directKey)).willReturn(Optional.empty());
        given(chatRoomCreateService.createDirectRoom(SENDER_ID, RECEIVER_ID, directKey)).willReturn(created);
        given(chatSaveService.save(SENDER_ID, created, CONTENT)).willReturn(saved);

        // when
        notifier.sendDirectSystemMessage(SENDER_ID, RECEIVER_ID, CONTENT);

        // then
        then(chatRoomCreateService).should().createDirectRoom(SENDER_ID, RECEIVER_ID, directKey);
        then(chatSaveService).should().save(SENDER_ID, created, CONTENT);
        then(eventPublisher).should().publishEvent(any(ChatMessageSentEvent.class));
    }
}
