package com.project.bangjjack.domain.checklist.presentation;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.usecase.ChecklistUseCase;
import com.project.bangjjack.domain.checklist.presentation.response.ChecklistResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Checklist", description = "생활습관 체크리스트 관련 API")
@RestController
@RequestMapping("/api/v1/checklist")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistUseCase checklistUseCase;

    @Operation(summary = "생활습관 체크리스트 등록", description = "사용자의 생활습관(취침/기상 시간, 잠버릇, 청소 주기 등)을 체크리스트로 등록합니다. 최초 1회만 가능합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> registerChecklist(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid LifestyleChecklistRequest request) {
        checklistUseCase.registerChecklist(memberId, request);
        return CommonResponse.success(ChecklistResponseCode.CHECKLIST_REGISTERED);
    }
}
