package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.application.exception.RoommateGroupMembershipNotFoundException;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.repository.RoommateGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoommateGroupMemberGetService {

    private final RoommateGroupMemberRepository roommateGroupMemberRepository;

    public boolean existsByUserIdAndRole(Long userId, GroupMemberRole role) {
        return roommateGroupMemberRepository.existsByUserIdAndRoleAndDeletedFalse(userId, role);
    }

    public long countByGroupIdAndRole(Long groupId, GroupMemberRole role) {
        return roommateGroupMemberRepository.countByGroupIdAndRoleAndDeletedFalse(groupId, role);
    }

    public RoommateGroupMember getActiveMembership(Long groupId, Long userId) {
        return roommateGroupMemberRepository.findActiveMembership(groupId, userId)
                .orElseThrow(RoommateGroupMembershipNotFoundException::new);
    }

    public List<RoommateGroupMember> getActiveMembersByGroupId(Long groupId) {
        return roommateGroupMemberRepository.findActiveMembersByGroupId(groupId);
    }

    public List<RoommateGroupMember> getMembershipsByUserId(Long userId) {
        return roommateGroupMemberRepository.findMembershipsByUserId(userId);
    }

    public List<RoommateGroupMember> getAllByGroupIds(Collection<Long> groupIds) {
        return roommateGroupMemberRepository.findAllByGroupIdIn(groupIds);
    }

    public List<RoommateGroupMember> getActiveMembersWithUserByPostId(Long postId) {
        return roommateGroupMemberRepository.findActiveMembersWithUserByPostId(postId);
    }
}
