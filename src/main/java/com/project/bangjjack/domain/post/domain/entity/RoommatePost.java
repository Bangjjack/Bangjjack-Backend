package com.project.bangjjack.domain.post.domain.entity;

import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roommate_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoommatePost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private RoomSize roomSize;

    @Column(nullable = false)
    private int recruitMemberCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PostStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Semester semester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Dormitory dormitory;

    public static RoommatePost create(
            User user,
            String title,
            String description,
            RoomSize roomSize,
            int recruitMemberCount,
            Semester semester,
            Dormitory dormitory
    ) {
        return new RoommatePost(user, title, description, roomSize, recruitMemberCount, PostStatus.OPEN, semester, dormitory);
    }

    public void close() {
        this.status = PostStatus.CLOSED;
    }
}
