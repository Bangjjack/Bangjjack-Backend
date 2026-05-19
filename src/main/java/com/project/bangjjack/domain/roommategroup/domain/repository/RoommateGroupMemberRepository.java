package com.project.bangjjack.domain.roommategroup.domain.repository;

import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoommateGroupMemberRepository extends JpaRepository<RoommateGroupMember, Long> {

    boolean existsByUserIdAndRoleAndDeletedFalse(Long userId, GroupMemberRole role);

    long countByGroupIdAndRoleAndDeletedFalse(Long groupId, GroupMemberRole role);
}
