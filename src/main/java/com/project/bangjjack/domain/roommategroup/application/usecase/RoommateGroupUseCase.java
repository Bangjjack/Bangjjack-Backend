package com.project.bangjjack.domain.roommategroup.application.usecase;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.application.dto.response.MyRoommateGroupResponse;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupDeleteService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberDeleteService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoommateGroupUseCase {

    private final RoommateGroupMemberGetService roommateGroupMemberGetService;
    private final RoommateGroupMemberDeleteService roommateGroupMemberDeleteService;
    private final RoommateGroupDeleteService roommateGroupDeleteService;
    private final RoommatePostGetService roommatePostGetService;
    private final RoommatePostDeleteService roommatePostDeleteService;

    @Transactional
    public void leaveRoommateGroup(Long userId, Long groupId) {
        RoommateGroupMember membership = roommateGroupMemberGetService.getActiveMembership(groupId, userId);

        if (membership.getRole() == GroupMemberRole.LEADER) {
            disbandGroup(membership.getGroup());
            return;
        }

        roommateGroupMemberDeleteService.delete(membership);
        membership.getGroup().getPost().open();
    }

    private void disbandGroup(RoommateGroup group) {
        List<RoommateGroupMember> members = roommateGroupMemberGetService.getActiveMembersByGroupId(group.getId());
        roommateGroupMemberDeleteService.deleteAll(members);

        RoommatePost post = group.getPost();
        PostSharedLifestyle sharedLifestyle = roommatePostGetService.getSharedLifestyleByPost(post);
        roommatePostDeleteService.deletePost(post, sharedLifestyle);

        roommateGroupDeleteService.delete(group);
    }

    public List<MyRoommateGroupResponse> getMyRoommateGroups(Long userId) {
        List<RoommateGroupMember> memberships = roommateGroupMemberGetService.getMembershipsByUserId(userId);
        if (memberships.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = memberships.stream()
                .map(membership -> membership.getGroup().getId())
                .toList();

        Map<Long, List<RoommateGroupMember>> membersByGroupId = roommateGroupMemberGetService.getAllByGroupIds(groupIds)
                .stream()
                .collect(Collectors.groupingBy(member -> member.getGroup().getId()));

        return memberships.stream()
                .map(RoommateGroupMember::getGroup)
                .map(group -> MyRoommateGroupResponse.from(
                        group,
                        membersByGroupId.getOrDefault(group.getId(), List.of())))
                .toList();
    }
}
