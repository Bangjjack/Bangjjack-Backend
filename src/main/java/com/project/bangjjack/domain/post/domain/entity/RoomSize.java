package com.project.bangjjack.domain.post.domain.entity;

public enum RoomSize {
    TWO_PERSON, THREE_PERSON, FOUR_PERSON;

    public boolean isValidRecruitCount(int count) {
        return switch (this) {
            case TWO_PERSON -> count == 1;
            case THREE_PERSON -> count >= 1 && count <= 2;
            case FOUR_PERSON -> count >= 1 && count <= 3;
        };
    }
}
