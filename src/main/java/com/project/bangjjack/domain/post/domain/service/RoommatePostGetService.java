package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePostGetService {

    private final RoommatePostRepository roommatePostRepository;

    public boolean existsOpenPostByUser(User user) {
        return roommatePostRepository.existsByUserAndStatusAndDeletedFalse(user, PostStatus.OPEN);
    }
}
