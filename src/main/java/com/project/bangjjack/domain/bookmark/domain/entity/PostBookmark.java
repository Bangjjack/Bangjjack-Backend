package com.project.bangjjack.domain.bookmark.domain.entity;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_bookmarks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostBookmark extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private RoommatePost post;

    public static PostBookmark create(User user, RoommatePost post) {
        return new PostBookmark(user, post);
    }
}