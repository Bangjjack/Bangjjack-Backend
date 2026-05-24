package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.checklist.application.dto.response.LifestyleChecklistResponse;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.user.domain.entity.User;

public record RoommateMemberDetailResponse(
        Long userId,
        String username,
        String profileImage,
        GroupMemberRole role,
        LifestyleChecklistResponse lifestyleChecklist
) {
    public static RoommateMemberDetailResponse from(RoommateGroupMember member, LifestyleChecklistResponse lifestyleChecklist) {
        User user = member.getUser();
        return new RoommateMemberDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImage(),
                member.getRole(),
                lifestyleChecklist
        );
    }
}
