package com.project.bangjjack.domain.roommategroup.domain.service;

import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateGroupDeleteService {

    public void delete(RoommateGroup group) {
        group.softDelete();
    }
}
