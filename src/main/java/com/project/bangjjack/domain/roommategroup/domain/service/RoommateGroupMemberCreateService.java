package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.repository.RoommateGroupMemberRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateGroupMemberCreateService {

    private final RoommateGroupMemberRepository roommateGroupMemberRepository;

    public RoommateGroupMember addMember(RoommateGroup group, User user, GroupMemberRole role) {
        return roommateGroupMemberRepository.save(RoommateGroupMember.create(group, user, role));
    }
}
