package com.project.bangjjack.domain.department.application.dto.response;

import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.user.domain.entity.Campus;

public record DepartmentResponse(Long departmentId, Campus campus, String name) {

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getCampus(), department.getName());
    }
}
