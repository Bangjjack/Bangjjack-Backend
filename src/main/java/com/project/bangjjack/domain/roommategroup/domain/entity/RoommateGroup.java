package com.project.bangjjack.domain.roommategroup.domain.entity;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "roommate_groups",
        uniqueConstraints = @UniqueConstraint(name = "UQ_group_post_id", columnNames = "post_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoommateGroup extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private RoommatePost post;

    public static RoommateGroup create(RoommatePost post) {
        return new RoommateGroup(post);
    }
}
