package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.application.dto.request.UpdateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.UpdateSharedLifestyleRequest;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import org.springframework.stereotype.Service;

@Service
public class RoommatePostUpdateService {

    public void updatePost(RoommatePost post, PostSharedLifestyle sharedLifestyle, UpdateRoommatePostRequest request) {
        post.update(request.title(), request.description(), request.roomSize(), request.recruitMemberCount());

        UpdateSharedLifestyleRequest lifestyle = request.sharedLifestyle();
        sharedLifestyle.update(
                lifestyle.roomTrashBinSharing(),
                lifestyle.recycling(),
                lifestyle.phoneCall(),
                lifestyle.itemSharing(),
                lifestyle.earphoneUsage(),
                lifestyle.lightsOutTime()
        );
    }
}
