package com.project.bangjjack.domain.department.presentation;

import com.project.bangjjack.domain.department.application.dto.response.DepartmentResponse;
import com.project.bangjjack.domain.department.application.usecase.DepartmentUseCase;
import com.project.bangjjack.domain.department.presentation.response.DepartmentResponseCode;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Department", description = "학과 관련 API")
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentUseCase departmentUseCase;

    @Operation(summary = "학과 목록 조회", description = "캠퍼스(GLOBAL_CAMPUS / MEDICAL_CAMPUS) 기준으로 학과 목록을 조회합니다. 온보딩 드롭다운 UI에서 사용됩니다.")
    @GetMapping
    public CommonResponse<List<DepartmentResponse>> getDepartmentsByCampus(
            @RequestParam String campus) {
        List<DepartmentResponse> response = departmentUseCase.getDepartmentsByCampus(campus);
        return CommonResponse.success(DepartmentResponseCode.DEPARTMENTS_FOUND, response);
    }
}
