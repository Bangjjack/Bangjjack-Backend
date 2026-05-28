package com.project.bangjjack.domain.user.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserResponseCode implements ResponseCodeInterface {

    ONBOARDING_COMPLETED(200, HttpStatus.OK, "온보딩이 완료되었습니다."),
    USER_BASIC_TAG_FOUND(200, HttpStatus.OK, "사용자 기본 태그 정보를 조회했습니다."),
    MY_PROFILE_FOUND(200, HttpStatus.OK, "내 프로필 정보를 조회했습니다."),
    USER_PROFILE_FOUND(200, HttpStatus.OK, "사용자 프로필 정보를 조회했습니다."),
    AI_RECOMMENDED_ROOMMATES_FOUND(200, HttpStatus.OK, "AI 추천 룸메이트 목록을 조회했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
