package com.project.bangjjack.domain.checklist.application.dto.response;

public record ChecklistField<T>(T value, Boolean matched) {

    public static <T> ChecklistField<T> of(T value, Boolean matched) {
        return new ChecklistField<>(value, matched);
    }
}
