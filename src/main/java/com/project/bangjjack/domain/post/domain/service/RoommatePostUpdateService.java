package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.Recycling;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import org.springframework.stereotype.Service;

@Service
public class RoommatePostUpdateService {

    public void updatePost(
            RoommatePost post, PostSharedLifestyle sharedLifestyle,
            String title, String description, RoomSize roomSize, int recruitMemberCount,
            boolean roomTrashBinSharing, Recycling recycling, PhoneCall phoneCall,
            ItemSharing itemSharing, boolean earphoneUsage, LightsOutTime lightsOutTime
    ) {
        post.update(title, description, roomSize, recruitMemberCount);
        sharedLifestyle.update(roomTrashBinSharing, recycling, phoneCall, itemSharing, earphoneUsage, lightsOutTime);
    }
}
