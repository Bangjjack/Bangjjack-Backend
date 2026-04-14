package com.project.bangjjack.domain.user.application.dto.request;

import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserOnboardingRequest(
        @NotNull Integer birthYear,
        @NotNull @Min(1) @Max(4) Integer grade,
        @NotNull Gender gender,
        @NotNull Campus campus,
        @NotNull Long departmentId,
        @NotNull Semester semester,
        @NotNull Dormitory dormitory
) {
}
