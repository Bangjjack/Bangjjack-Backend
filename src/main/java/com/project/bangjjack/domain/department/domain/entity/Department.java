package com.project.bangjjack.domain.department.domain.entity;

import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "departments", indexes = {
        @Index(name = "idx_departments_campus", columnList = "campus")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Department extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Campus campus;

    public static Department create(String name, Campus campus) {
        return new Department(name, campus);
    }
}
