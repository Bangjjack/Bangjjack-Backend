package com.project.bangjjack.domain.user.domain.entity;

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

    @Column(nullable = false, unique = true, length = 255)
    private String providerId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private Integer age;

    @Column(length = 512)
    private String profileImage;

    public static User create(String providerId, String username, String email, String profileImage) {
        return new User(providerId, username, email, null, null, profileImage);
    }

    public void updateProfile(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }
}
