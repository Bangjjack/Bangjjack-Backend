package com.project.bangjjack.domain.roommategroup.application.dto.response;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;

import java.util.List;

public record MyRoommateGroupResponse(
        Long groupId,
        Long postId,
        String postTitle,
        RoomSize roomSize,
        Dormitory dormitory,
        int totalCapacity,
        int currentMemberCount,
        List<GroupMemberResponse> members
) {
    public static MyRoommateGroupResponse from(RoommateGroup group, List<RoommateGroupMember> members) {
        RoommatePost post = group.getPost();
        return new MyRoommateGroupResponse(
                group.getId(),
                post.getId(),
                post.getTitle(),
                post.getRoomSize(),
                post.getDormitory(),
                post.getRecruitMemberCount() + 1,
                members.size(),
                members.stream()
                        .map(GroupMemberResponse::from)
                        .toList()
        );
    }
}
