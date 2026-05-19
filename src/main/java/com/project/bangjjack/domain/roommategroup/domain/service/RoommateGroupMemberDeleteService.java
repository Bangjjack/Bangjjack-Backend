package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateGroupMemberDeleteService {

    public void delete(RoommateGroupMember member) {
        member.softDelete();
    }
}
