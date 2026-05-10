package com.project.bangjjack.domain.post.application.dto.request;

import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.Recycling;
import jakarta.validation.constraints.NotNull;

public record CreateSharedLifestyleRequest(

        @NotNull Boolean roomTrashBinSharing,

        @NotNull Recycling recycling,

        @NotNull PhoneCall phoneCall,

        @NotNull ItemSharing itemSharing,

        @NotNull Boolean earphoneUsage,

        @NotNull LightsOutTime lightsOutTime
) {
}
