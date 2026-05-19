package com.project.bangjjack.domain.roommategroup.application.usecase;

import com.project.bangjjack.domain.roommategroup.application.dto.response.MyRoommateGroupResponse;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
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
