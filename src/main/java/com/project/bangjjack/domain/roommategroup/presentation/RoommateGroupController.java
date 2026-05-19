package com.project.bangjjack.domain.roommategroup.presentation;

import com.project.bangjjack.domain.roommategroup.application.dto.response.MyRoommateGroupResponse;
import com.project.bangjjack.domain.roommategroup.application.usecase.RoommateGroupUseCase;
import com.project.bangjjack.domain.roommategroup.presentation.response.RoommateGroupResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "RoommateGroup", description = "룸메이트 그룹 관련 API")
@RestController
@RequestMapping("/api/v1/roommate-groups")
@RequiredArgsConstructor
public class RoommateGroupController {

    private final RoommateGroupUseCase roommateGroupUseCase;

    @Operation(summary = "자신이 속한 룸메이트 그룹 목록 조회", description = "요청자가 소속된 모든 룸메이트 그룹(LEADER/MEMBER, 0~2개)을 조회합니다. 소속 그룹이 없으면 빈 배열을 반환합니다.")
    @GetMapping("/me")
    public CommonResponse<List<MyRoommateGroupResponse>> getMyRoommateGroups(
            @CurrentMemberId Long memberId) {
        List<MyRoommateGroupResponse> response = roommateGroupUseCase.getMyRoommateGroups(memberId);
        return CommonResponse.success(RoommateGroupResponseCode.MY_ROOMMATE_GROUPS_FOUND, response);
    }
}
