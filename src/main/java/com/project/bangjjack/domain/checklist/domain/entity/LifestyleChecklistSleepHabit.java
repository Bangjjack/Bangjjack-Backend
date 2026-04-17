package com.project.bangjjack.domain.checklist.domain.entity;

import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lifestyle_checklist_sleep_habits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LifestyleChecklistSleepHabit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private LifestyleChecklist checklist;

    @Enumerated(EnumType.STRING)
    @Column(name = "sleep_habit", nullable = false, length = 25)
    private SleepHabit sleepHabit;

    public static LifestyleChecklistSleepHabit create(LifestyleChecklist checklist, SleepHabit sleepHabit) {
        return new LifestyleChecklistSleepHabit(checklist, sleepHabit);
    }
}
