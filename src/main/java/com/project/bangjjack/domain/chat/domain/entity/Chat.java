package com.project.bangjjack.domain.chat.domain.entity;

import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "chat",
    indexes = @Index(name = "IDX_chat_room_id", columnList = "room_id, id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Chat extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    public static Chat create(Long senderId, ChatRoom chatRoom, String content, MessageType messageType) {
        return new Chat(senderId, chatRoom, content, messageType);
    }
}
