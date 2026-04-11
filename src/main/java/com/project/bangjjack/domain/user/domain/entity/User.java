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

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Gender gender;

	@Column(nullable = false)
	private int age;

	@Column(length = 512)
	private String profileImage;

	public static User create(String username, String email, Gender gender, int age, String profileImage) {
		return new User(username, email, gender, age, profileImage);
	}

	public void updateProfile(String username, String profileImage) {
		this.username = username;
		this.profileImage = profileImage;
	}
}
