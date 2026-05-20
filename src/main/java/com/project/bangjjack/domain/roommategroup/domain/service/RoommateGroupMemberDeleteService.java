package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoommateGroupMemberDeleteService {

    public void delete(RoommateGroupMember member) {
        member.softDelete();
    }

    public void deleteAll(List<RoommateGroupMember> members) {
        members.forEach(RoommateGroupMember::softDelete);
    }
}
