package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.PostSharedLifestyleRepository;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePostDeleteService {

    private final RoommatePostRepository roommatePostRepository;
    private final PostSharedLifestyleRepository postSharedLifestyleRepository;

    public void deletePost(RoommatePost post, PostSharedLifestyle sharedLifestyle) {
        post.softDelete();
        sharedLifestyle.softDelete();
        roommatePostRepository.save(post);
        postSharedLifestyleRepository.save(sharedLifestyle);
    }
}
