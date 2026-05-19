package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.repository.RoommateGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateGroupCreateService {

    private final RoommateGroupRepository roommateGroupRepository;

    public RoommateGroup createGroup(RoommatePost post) {
        return roommateGroupRepository.save(RoommateGroup.create(post));
    }
}
