package com.project.bangjjack.domain.user.domain.entity;

import java.util.Arrays;
import java.util.List;

public enum Dormitory {
    DORM_1(Campus.GLOBAL_CAMPUS),
    DORM_2(Campus.GLOBAL_CAMPUS),
    DORM_3(Campus.GLOBAL_CAMPUS);

    private final Campus campus;

    Dormitory(Campus campus) {
        this.campus = campus;
    }

    public static List<Dormitory> ofCampus(Campus campus) {
        List<Dormitory> dormitories = Arrays.stream(values())
                .filter(d -> d.campus == campus)
                .toList();
        if (dormitories.isEmpty()) {
            throw new IllegalStateException("No dormitories mapped for campus: " + campus);
        }
        return dormitories;
    }
}
