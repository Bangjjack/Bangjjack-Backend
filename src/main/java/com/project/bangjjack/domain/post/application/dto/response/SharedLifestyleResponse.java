package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.Recycling;

public record SharedLifestyleResponse(
        boolean roomTrashBinSharing,
        Recycling recycling,
        PhoneCall phoneCall,
        ItemSharing itemSharing,
        boolean earphoneUsage,
        LightsOutTime lightsOutTime
) {
    public static SharedLifestyleResponse from(PostSharedLifestyle sharedLifestyle) {
        return new SharedLifestyleResponse(
                sharedLifestyle.isRoomTrashBinSharing(),
                sharedLifestyle.getRecycling(),
                sharedLifestyle.getPhoneCall(),
                sharedLifestyle.getItemSharing(),
                sharedLifestyle.isEarphoneUsage(),
                sharedLifestyle.getLightsOutTime()
        );
    }
}
