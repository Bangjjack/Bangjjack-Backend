package com.project.bangjjack.domain.roommategroup.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoommateGroupResponseCode implements ResponseCodeInterface {

    MY_ROOMMATE_GROUPS_FOUND(200, HttpStatus.OK, "내가 속한 룸메이트 그룹 목록을 조회했습니다."),
    ROOMMATE_GROUP_LEFT(200, HttpStatus.OK, "룸메이트 그룹에서 탈퇴했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
