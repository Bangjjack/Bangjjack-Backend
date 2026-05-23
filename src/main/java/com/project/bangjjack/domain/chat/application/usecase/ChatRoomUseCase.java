package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.CreateChatRoomRequest;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomCategory;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomListResponse;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomResponse;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomSummaryResponse;
import com.project.bangjjack.domain.chat.application.event.ChatRoomCreatedEvent;
import com.project.bangjjack.domain.chat.application.exception.CannotChatWithSelfException;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.service.ChatMessageGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomUseCase {

    private final ChatRoomCreateService chatRoomCreateService;
    private final ChatRoomGetService chatRoomGetService;
    private final ChatMessageGetService chatMessageGetService;
    private final UserGetService userGetService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ChatRoomResponse createDirectRoom(Long currentUserId, CreateChatRoomRequest request) {
        if (currentUserId.equals(request.targetUserId())) {
            throw new CannotChatWithSelfException();
        }
        userGetService.getById(request.targetUserId());

        String directRoomKey = chatRoomCreateService.createDirectKey(currentUserId, request.targetUserId());

        Optional<ChatRoom> existing = chatRoomGetService.findByDirectRoomKey(directRoomKey);
        if (existing.isPresent()) {
            ChatRoom chatRoom = existing.get();
            List<ChatRoomParticipant> participants = chatRoomGetService.findParticipantsByRoomId(chatRoom.getId());
            return ChatRoomResponse.from(chatRoom, participants, false);
        }

        ChatRoom chatRoom = chatRoomCreateService.createDirectRoom(currentUserId, request.targetUserId(), directRoomKey);
        List<ChatRoomParticipant> participants = chatRoomGetService.findParticipantsByRoomId(chatRoom.getId());
        eventPublisher.publishEvent(new ChatRoomCreatedEvent(chatRoom.getId(), List.of(currentUserId, request.targetUserId())));
        return ChatRoomResponse.from(chatRoom, participants, true);
    }

    public ChatRoomListResponse getMyChatRooms(Long userId, ChatRoomCategory category, Long cursor, int size) {
        List<ChatRoomParticipant> allParticipants = chatRoomGetService.findParticipantsPage(userId, cursor, size, category);

        // 현재 유저의 participant (unreadCount 조회용)
        Map<Long, ChatRoomParticipant> myParticipantByRoomId = allParticipants.stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(Collectors.toMap(p -> p.getChatRoom().getId(), p -> p));

        // 파트너 userId (roomId → partnerId)
        Map<Long, Long> partnerIdByRoomId = allParticipants.stream()
                .filter(p -> !p.getUserId().equals(userId))
                .collect(Collectors.toMap(p -> p.getChatRoom().getId(), ChatRoomParticipant::getUserId));

        boolean hasNext = myParticipantByRoomId.size() > size;

        // 마지막 메시지 기준 내림차순 재정렬 후 size로 자르기
        List<ChatRoomParticipant> sortedPage = myParticipantByRoomId.values().stream()
                .sorted(Comparator
                        .comparing((ChatRoomParticipant p) -> p.getChatRoom().getLastMessageAt(),
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(p -> p.getChatRoom().getId(), Comparator.reverseOrder()))
                .limit(size)
                .toList();

        Map<Long, User> partnerMap = userGetService.getByIds(partnerIdByRoomId.values());

        List<Long> roomIds = sortedPage.stream().map(p -> p.getChatRoom().getId()).toList();
        Map<Long, Chat> lastMessageMap = chatMessageGetService.findLastMessagesByRoomIds(roomIds);

        List<ChatRoomSummaryResponse> rooms = new ArrayList<>();
        for (ChatRoomParticipant participant : sortedPage) {
            ChatRoom room = participant.getChatRoom();
            User partner = partnerMap.get(partnerIdByRoomId.get(room.getId()));
            Chat lastChat = lastMessageMap.get(room.getId());
            rooms.add(ChatRoomSummaryResponse.of(room, partner, lastChat, participant.getUnreadCount()));
        }

        Long nextCursor = hasNext && !sortedPage.isEmpty()
                ? sortedPage.get(sortedPage.size() - 1).getChatRoom().getId()
                : null;
        return ChatRoomListResponse.of(rooms, nextCursor, hasNext);
    }

    public List<Long> getMyRoomIds(Long userId) {
        return chatRoomGetService.findRoomIdsByUserId(userId);
    }

    public void validateParticipant(Long roomId, Long userId) {
        chatRoomGetService.validateParticipant(roomId, userId);
    }
}
