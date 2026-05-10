package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePostDeleteService {

    private final RoommatePostRepository roommatePostRepository;

    public void deletePost(RoommatePost post) {
        post.softDelete();
        roommatePostRepository.save(post);
    }
}
