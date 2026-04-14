package com.project.bangjjack.domain.user.domain.entity;

import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String providerId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private Integer birthYear;

    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Campus campus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Semester semester;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Dormitory dormitory;

    @Column(nullable = false)
    private boolean isOnboarded = false;

    @Column(length = 512)
    private String profileImage;

    public static User create(String providerId, String username, String email, String profileImage) {
        return new User(providerId, username, email, null, null, null, null, null, null, null, false, profileImage);
    }

    public void completeOnboarding(Integer birthYear, Integer grade, Gender gender,
                                   Campus campus, Department department,
                                   Semester semester, Dormitory dormitory) {
        this.birthYear = birthYear;
        this.grade = grade;
        this.gender = gender;
        this.campus = campus;
        this.department = department;
        this.semester = semester;
        this.dormitory = dormitory;
        this.isOnboarded = true;
    }

    public void updateProfile(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }
}
