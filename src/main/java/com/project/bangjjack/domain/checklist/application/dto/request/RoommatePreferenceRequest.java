package com.project.bangjjack.domain.checklist.application.dto.request;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RoommatePreferenceRequest(
        @NotNull @Size(min = 3, max = 3) List<RoommatePreferenceFactor> preferences
) {
}
