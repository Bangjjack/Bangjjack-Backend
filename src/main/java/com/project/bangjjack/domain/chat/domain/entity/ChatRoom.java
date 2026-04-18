package com.project.bangjjack.domain.chat.domain.entity;

import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "chat_room",
    uniqueConstraints = @UniqueConstraint(name = "UQ_chat_room_direct_key", columnNames = "direct_room_key")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom extends BaseEntity {

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 10)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RoomStatus status;

    @Column(name = "direct_room_key", length = 50)
    private String directRoomKey;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomParticipant> participants = new ArrayList<>();

    public static ChatRoom createDirect(Long createdBy, String directRoomKey) {
        return new ChatRoom(createdBy, RoomType.DIRECT, RoomStatus.OPEN, directRoomKey, new ArrayList<>());
    }

    public static String generateDirectKey(Long userId1, Long userId2) {
        return "DM_" + Math.min(userId1, userId2) + "_" + Math.max(userId1, userId2);
    }

    public void addParticipant(ChatRoomParticipant participant) {
        participants.add(participant);
    }

    public void close() {
        this.status = RoomStatus.CLOSED;
    }
}
