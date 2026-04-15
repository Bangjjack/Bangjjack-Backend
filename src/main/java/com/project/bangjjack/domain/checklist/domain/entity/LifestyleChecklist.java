package com.project.bangjjack.domain.checklist.domain.entity;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lifestyle_checklists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LifestyleChecklist extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Bedtime bedtime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WakeUpTime wakeUpTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CleaningCycle cleaningCycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DormStayTime dormStayTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CallHabit callHabit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IndoorTemperature indoorTemperature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoiseSensitivity noiseSensitivity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private Smoking smoking;

    public static LifestyleChecklist create(User user, LifestyleChecklistRequest request) {
        return new LifestyleChecklist(
                user,
                request.bedtime(),
                request.wakeUpTime(),
                request.cleaningCycle(),
                request.dormStayTime(),
                request.callHabit(),
                request.indoorTemperature(),
                request.noiseSensitivity(),
                request.smoking()
        );
    }
}
