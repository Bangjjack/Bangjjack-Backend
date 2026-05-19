package com.project.bangjjack.domain.roommategroup.presentation;

import com.project.bangjjack.domain.roommategroup.application.dto.response.MyRoommateGroupResponse;
import com.project.bangjjack.domain.roommategroup.application.usecase.RoommateGroupUseCase;
import com.project.bangjjack.domain.roommategroup.presentation.response.RoommateGroupResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "룸메이트 그룹 탈퇴", description = "요청자가 본인의 멤버십을 Soft Delete하여 그룹에서 탈퇴합니다. MEMBER 탈퇴 시 빈자리가 생기면 연결된 모집글이 CLOSED였을 경우 OPEN으로 복귀합니다. LEADER가 탈퇴하면 그룹이 해체되어 연결된 모집글·공유 라이프스타일·그룹·모든 멤버십이 함께 Soft Delete됩니다(신청 이력은 보존).")
    @DeleteMapping("/{groupId}/members/me")
    public CommonResponse<Void> leaveRoommateGroup(
            @CurrentMemberId Long memberId,
            @PathVariable Long groupId) {
        roommateGroupUseCase.leaveRoommateGroup(memberId, groupId);
        return CommonResponse.success(RoommateGroupResponseCode.ROOMMATE_GROUP_LEFT);
    }
}
