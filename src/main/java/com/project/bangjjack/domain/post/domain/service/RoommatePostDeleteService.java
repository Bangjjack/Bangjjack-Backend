package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import org.springframework.stereotype.Service;

@Service
public class RoommatePostDeleteService {

    public void deletePost(RoommatePost post, PostSharedLifestyle sharedLifestyle) {
        post.softDelete();
        sharedLifestyle.softDelete();
    }
}
