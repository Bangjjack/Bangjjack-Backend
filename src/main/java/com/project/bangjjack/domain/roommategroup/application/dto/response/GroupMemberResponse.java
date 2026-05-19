package com.project.bangjjack.domain.roommategroup.application.dto.response;

import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.user.domain.entity.User;

public record GroupMemberResponse(
        Long userId,
        String username,
        String profileImage,
        GroupMemberRole role
) {
    public static GroupMemberResponse from(RoommateGroupMember member) {
        User user = member.getUser();
        return new GroupMemberResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImage(),
                member.getRole()
        );
    }
}
