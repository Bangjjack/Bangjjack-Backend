package com.project.bangjjack.domain.roommategroup.domain.entity;

import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "roommate_group_members",
        uniqueConstraints = @UniqueConstraint(name = "UQ_group_member_group_user", columnNames = {"group_id", "user_id"}),
        indexes = @Index(name = "IDX_group_member_user_role", columnList = "user_id, role")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoommateGroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private RoommateGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GroupMemberRole role;

    public static RoommateGroupMember create(RoommateGroup group, User user, GroupMemberRole role) {
        return new RoommateGroupMember(group, user, role);
    }
}
