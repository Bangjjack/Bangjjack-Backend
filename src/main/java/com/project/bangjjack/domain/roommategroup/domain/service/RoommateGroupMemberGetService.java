package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.repository.RoommateGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
