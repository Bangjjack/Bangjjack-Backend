package com.project.bangjjack.domain.application.application.dto.response;

import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;

import java.time.LocalDateTime;

public record ProcessRoommateApplicationResponse(
        Long applicationId,
        Long postId,
        Long applicantId,
        ApplicationStatus status,
        Long chatRoomId,
        Long groupId,
        Integer currentGroupMemberCount,
        LocalDateTime processedAt
) {
    public static ProcessRoommateApplicationResponse from(
            RoommateApplication application,
            Long chatRoomId,
            Long groupId,
            Integer currentGroupMemberCount
    ) {
        return new ProcessRoommateApplicationResponse(
                application.getId(),
                application.getPost().getId(),
                application.getApplicant().getId(),
                application.getStatus(),
                chatRoomId,
                groupId,
                currentGroupMemberCount,
                application.getUpdatedAt()
        );
    }
}
