package com.project.bangjjack.domain.application.application.dto.response;

import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;

import java.time.LocalDateTime;

public record CreateRoommateApplicationResponse(
        Long applicationId,
        Long postId,
        Long applicantId,
        ApplicationStatus status,
        Long chatRoomId,
        boolean isNewChatRoom,
        LocalDateTime createdAt
) {
    public static CreateRoommateApplicationResponse from(RoommateApplication application, Long chatRoomId, boolean isNewChatRoom) {
        return new CreateRoommateApplicationResponse(
                application.getId(),
                application.getPost().getId(),
                application.getApplicant().getId(),
                application.getStatus(),
                chatRoomId,
                isNewChatRoom,
                application.getCreatedAt()
        );
    }
}
