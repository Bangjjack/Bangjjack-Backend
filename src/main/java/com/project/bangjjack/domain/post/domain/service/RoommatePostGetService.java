package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.application.exception.SharedLifestyleNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.PostSharedLifestyleRepository;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePostGetService {

    private final RoommatePostRepository roommatePostRepository;
    private final PostSharedLifestyleRepository postSharedLifestyleRepository;

    public boolean existsOpenPostByUser(User user) {
        return roommatePostRepository.existsByUserAndStatusAndDeletedFalse(user, PostStatus.OPEN);
    }

    public RoommatePost getById(Long postId) {
        return roommatePostRepository.findWithUserByIdAndDeletedFalse(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    public PostSharedLifestyle getSharedLifestyleByPost(RoommatePost post) {
        return postSharedLifestyleRepository.findByPost(post)
                .orElseThrow(SharedLifestyleNotFoundException::new);
    }

    public PostSharedLifestyle getSharedLifestyleWithPostAndUserByPostId(Long postId) {
        return postSharedLifestyleRepository.findWithPostAndUserByPostId(postId)
                .orElseThrow(PostNotFoundException::new);
    }
}
