package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.application.application.exception.RoommateGroupNotFoundException;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.repository.RoommateGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateGroupGetService {

    private final RoommateGroupRepository roommateGroupRepository;

    public RoommateGroup getByPostId(Long postId) {
        return roommateGroupRepository.findByPostIdAndDeletedFalse(postId)
                .orElseThrow(RoommateGroupNotFoundException::new);
    }

    public RoommateGroup getByPostIdForUpdate(Long postId) {
        return roommateGroupRepository.findByPostIdAndDeletedFalseForUpdate(postId)
                .orElseThrow(RoommateGroupNotFoundException::new);
    }
}
