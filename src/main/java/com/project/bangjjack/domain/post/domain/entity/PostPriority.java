package com.project.bangjjack.domain.post.domain.entity;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_priorities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostPriority extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private RoommatePost post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoommatePreferenceFactor firstPriority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoommatePreferenceFactor secondPriority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoommatePreferenceFactor thirdPriority;

    public static PostPriority create(
            RoommatePost post,
            RoommatePreferenceFactor firstPriority,
            RoommatePreferenceFactor secondPriority,
            RoommatePreferenceFactor thirdPriority
    ) {
        return new PostPriority(post, firstPriority, secondPriority, thirdPriority);
    }
}
