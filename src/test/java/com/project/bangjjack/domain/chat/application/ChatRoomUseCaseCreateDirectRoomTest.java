package com.project.bangjjack.domain.chat.application;

import com.project.bangjjack.domain.chat.application.dto.request.CreateChatRoomRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomResponse;
import com.project.bangjjack.domain.chat.application.exception.CannotChatWithSelfException;
import com.project.bangjjack.domain.chat.application.usecase.ChatRoomUseCase;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.user.application.exception.UserNotFoundException;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatRoomUseCaseCreateDirectRoomTest {

    @Mock
    private ChatRoomCreateService chatRoomCreateService;

    @Mock
    private ChatRoomGetService chatRoomGetService;

    @Mock
    private UserGetService userGetService;

    @InjectMocks
    private ChatRoomUseCase chatRoomUseCase;

    @Nested
    @DisplayName("1:1 채팅방 생성 시")
    class CreateDirectRoom {

        @Test
        @DisplayName("채팅방이 존재하지 않으면 신규 생성되고 isNewRoom이 true이다")
        void 채팅방이_없으면_신규_생성_성공() {
            // given
            Long currentUserId = 1L;
            Long targetUserId = 2L;
            CreateChatRoomRequest request = new CreateChatRoomRequest(targetUserId);

            ChatRoom chatRoom = ChatRoom.createDirect(currentUserId, "DM_1_2");
            chatRoom.addParticipant(ChatRoomParticipant.create(chatRoom, currentUserId));
            chatRoom.addParticipant(ChatRoomParticipant.create(chatRoom, targetUserId));

            given(userGetService.getById(targetUserId)).willReturn(null);
            given(chatRoomGetService.findByDirectRoomKey(anyString())).willReturn(Optional.empty());
            given(chatRoomCreateService.createDirectRoom(anyLong(), anyLong(), anyString())).willReturn(chatRoom);

            // when
            ChatRoomResponse response = chatRoomUseCase.createDirectRoom(currentUserId, request);

            // then
            assertThat(response.isNewRoom()).isTrue();
            assertThat(response.roomType()).isEqualTo("DIRECT");
            assertThat(response.participants()).hasSize(2);
        }

        @Test
        @DisplayName("동일 두 유저 간 채팅방이 이미 존재하면 기존 방을 반환하고 isNewRoom이 false이다")
        void 채팅방이_이미_존재하면_기존_방_반환() {
            // given
            Long currentUserId = 1L;
            Long targetUserId = 2L;
            CreateChatRoomRequest request = new CreateChatRoomRequest(targetUserId);

            ChatRoom existingRoom = ChatRoom.createDirect(currentUserId, "DM_1_2");
            existingRoom.addParticipant(ChatRoomParticipant.create(existingRoom, currentUserId));
            existingRoom.addParticipant(ChatRoomParticipant.create(existingRoom, targetUserId));

            given(userGetService.getById(targetUserId)).willReturn(null);
            given(chatRoomGetService.findByDirectRoomKey(anyString())).willReturn(Optional.of(existingRoom));

            // when
            ChatRoomResponse response = chatRoomUseCase.createDirectRoom(currentUserId, request);

            // then
            assertThat(response.isNewRoom()).isFalse();
            assertThat(response.participants()).hasSize(2);
        }

        @Test
        @DisplayName("B→A 방향으로 요청해도 A→B로 생성된 채팅방을 반환한다")
        void 역방향_요청도_동일한_채팅방을_반환한다() {
            // given
            Long userA = 1L;
            Long userB = 2L;
            // A→B로 이미 생성된 방이 있다고 가정
            ChatRoom existingRoom = ChatRoom.createDirect(userA, "DM_1_2");
            existingRoom.addParticipant(ChatRoomParticipant.create(existingRoom, userA));
            existingRoom.addParticipant(ChatRoomParticipant.create(existingRoom, userB));

            // B→A 방향으로 요청
            CreateChatRoomRequest request = new CreateChatRoomRequest(userA);
            given(userGetService.getById(userA)).willReturn(null);
            given(chatRoomGetService.findByDirectRoomKey("DM_1_2")).willReturn(Optional.of(existingRoom));

            // when
            ChatRoomResponse response = chatRoomUseCase.createDirectRoom(userB, request);

            // then
            assertThat(response.isNewRoom()).isFalse();
        }

        @Test
        @DisplayName("자기 자신에게 채팅방 생성을 요청하면 CannotChatWithSelfException이 발생한다")
        void 자기_자신에게_요청하면_예외_발생() {
            // given
            Long currentUserId = 1L;
            CreateChatRoomRequest request = new CreateChatRoomRequest(currentUserId);

            // when & then
            assertThatThrownBy(() -> chatRoomUseCase.createDirectRoom(currentUserId, request))
                    .isInstanceOf(CannotChatWithSelfException.class);
        }

        @Test
        @DisplayName("존재하지 않는 유저에게 채팅방 생성을 요청하면 UserNotFoundException이 발생한다")
        void 존재하지_않는_유저에게_요청하면_예외_발생() {
            // given
            Long currentUserId = 1L;
            Long notExistUserId = 999L;
            CreateChatRoomRequest request = new CreateChatRoomRequest(notExistUserId);

            given(userGetService.getById(notExistUserId)).willThrow(new UserNotFoundException());

            // when & then
            assertThatThrownBy(() -> chatRoomUseCase.createDirectRoom(currentUserId, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("generateDirectKey 생성 시")
    class GenerateDirectKey {

        @Test
        @DisplayName("userId 순서와 관계없이 항상 동일한 키를 생성한다")
        void userId_순서_무관하게_동일_키_생성() {
            // when
            String keyAtoB = ChatRoom.generateDirectKey(5L, 12L);
            String keyBtoA = ChatRoom.generateDirectKey(12L, 5L);

            // then
            assertThat(keyAtoB).isEqualTo(keyBtoA).isEqualTo("DM_5_12");
        }
    }
}
