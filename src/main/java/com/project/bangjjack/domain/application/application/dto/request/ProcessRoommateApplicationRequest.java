package com.project.bangjjack.domain.application.application.dto.request;

import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ProcessRoommateApplicationRequest(

        @NotNull ApplicationStatus status
) {
}
