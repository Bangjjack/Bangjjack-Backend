package com.project.bangjjack.domain.post.domain.service;

import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.application.exception.SharedLifestyleNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.PostSharedLifestyleRepository;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostRepository;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public RoommatePost getOpenById(Long postId) {
        return roommatePostRepository.findWithUserByIdAndStatusAndDeletedFalse(postId, PostStatus.OPEN)
                .orElseThrow(PostNotFoundException::new);
    }

    public RoommatePost getByIdForUpdate(Long postId) {
        return roommatePostRepository.findByIdAndDeletedFalseForUpdate(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    public Optional<RoommatePost> findOpenWithUserByUserIdForUpdate(Long userId) {
        return roommatePostRepository.findWithUserByUserIdAndStatusAndDeletedFalseForUpdate(userId, PostStatus.OPEN);
    }

    public PostSharedLifestyle getSharedLifestyleByPost(RoommatePost post) {
        return postSharedLifestyleRepository.findByPostAndDeletedFalse(post)
                .orElseThrow(SharedLifestyleNotFoundException::new);
    }

    public PostSharedLifestyle getSharedLifestyleWithPostAndUserByPostId(Long postId) {
        return postSharedLifestyleRepository.findWithPostAndUserByPostId(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    public Slice<RoommatePost> getPostList(Campus campus, RoomSize roomSize, Pageable pageable) {
        return roommatePostRepository.findPostList(campus, roomSize, pageable);
    }
}
