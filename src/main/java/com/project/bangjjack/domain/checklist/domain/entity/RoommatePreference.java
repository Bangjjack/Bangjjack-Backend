package com.project.bangjjack.domain.checklist.domain.entity;

import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roommate_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoommatePreference extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoommatePreferenceFactor firstPriority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoommatePreferenceFactor secondPriority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoommatePreferenceFactor thirdPriority;

    public static RoommatePreference create(
            User user,
            RoommatePreferenceFactor firstPriority,
            RoommatePreferenceFactor secondPriority,
            RoommatePreferenceFactor thirdPriority
    ) {
        return new RoommatePreference(user, firstPriority, secondPriority, thirdPriority);
    }
}
