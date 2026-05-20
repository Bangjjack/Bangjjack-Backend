package com.project.bangjjack.domain.roommategroup.application.event;

import java.util.List;

public record RoommateGroupDisbandedEvent(
        Long leaderId,
        List<Long> memberIds,
        String postTitle
) {
}
