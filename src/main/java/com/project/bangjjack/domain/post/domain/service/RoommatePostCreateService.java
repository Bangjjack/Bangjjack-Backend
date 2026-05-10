package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.PostSharedLifestyleRepository;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePostCreateService {

    private final RoommatePostRepository roommatePostRepository;
    private final PostSharedLifestyleRepository postSharedLifestyleRepository;

    public void createPost(RoommatePost post, PostSharedLifestyle sharedLifestyle) {
        roommatePostRepository.save(post);
        postSharedLifestyleRepository.save(sharedLifestyle);
    }
}
