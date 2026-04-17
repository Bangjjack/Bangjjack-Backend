package com.project.bangjjack.domain.department.application.usecase;

import com.project.bangjjack.domain.department.application.dto.response.DepartmentResponse;
import com.project.bangjjack.domain.department.application.exception.InvalidCampusException;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentUseCase {

    private final DepartmentGetService departmentGetService;

    public List<DepartmentResponse> getDepartmentsByCampus(String campusStr) {
        Campus campus;
        try {
            campus = Campus.valueOf(campusStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidCampusException();
        }

        return departmentGetService.findAllByCampus(campus).stream()
                .map(DepartmentResponse::from)
                .toList();
    }
}
