package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.repository.RoommateGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateGroupMemberDeleteService {

    private final RoommateGroupMemberRepository roommateGroupMemberRepository;

    public void delete(RoommateGroupMember member) {
        member.softDelete();
        roommateGroupMemberRepository.save(member);
    }
}
