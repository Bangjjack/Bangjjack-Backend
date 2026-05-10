package com.project.bangjjack.domain.post.application.dto.request;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRoommatePostRequest(

        @NotBlank @Size(min = 1, max = 40) String title,

        @NotNull RoomSize roomSize,

        @NotNull @Min(1) Integer recruitMemberCount,

        @NotBlank @Size(min = 1, max = 500) String description,

        @NotNull @Valid UpdateSharedLifestyleRequest sharedLifestyle
) {
}
