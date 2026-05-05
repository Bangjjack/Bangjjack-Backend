package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.PostPriority;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.PostPriorityRepository;
import com.project.bangjjack.domain.post.domain.repository.PostSharedLifestyleRepository;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePostCreateService {

    private final RoommatePostRepository roommatePostRepository;
    private final PostPriorityRepository postPriorityRepository;
    private final PostSharedLifestyleRepository postSharedLifestyleRepository;

    public void createPost(RoommatePost post, PostPriority priority, PostSharedLifestyle sharedLifestyle) {
        roommatePostRepository.save(post);
        postPriorityRepository.save(priority);
        postSharedLifestyleRepository.save(sharedLifestyle);
    }
}
