package com.project.bangjjack.domain.roommategroup.domain.repository;

import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoommateGroupMemberRepository extends JpaRepository<RoommateGroupMember, Long> {

    boolean existsByUserIdAndRoleAndDeletedFalse(Long userId, GroupMemberRole role);

    long countByGroupIdAndRoleAndDeletedFalse(Long groupId, GroupMemberRole role);

    @Query("SELECT m FROM RoommateGroupMember m "
            + "JOIN FETCH m.group g "
            + "JOIN FETCH g.post "
            + "WHERE g.id = :groupId AND m.user.id = :userId AND m.deleted = false")
    Optional<RoommateGroupMember> findActiveMembership(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT m FROM RoommateGroupMember m "
            + "WHERE m.group.id = :groupId AND m.deleted = false")
    List<RoommateGroupMember> findActiveMembersByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT m FROM RoommateGroupMember m "
            + "JOIN FETCH m.group g "
            + "JOIN FETCH g.post "
            + "WHERE m.user.id = :userId AND m.deleted = false")
    List<RoommateGroupMember> findMembershipsByUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM RoommateGroupMember m "
            + "JOIN FETCH m.user "
            + "WHERE m.group.id IN :groupIds AND m.deleted = false")
    List<RoommateGroupMember> findAllByGroupIdIn(@Param("groupIds") Collection<Long> groupIds);
}
