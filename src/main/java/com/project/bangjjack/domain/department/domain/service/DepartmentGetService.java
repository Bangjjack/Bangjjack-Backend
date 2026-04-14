package com.project.bangjjack.domain.department.domain.service;

import com.project.bangjjack.domain.department.application.exception.DepartmentNotFoundException;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentGetService {

    private final DepartmentRepository departmentRepository;

    public Department getById(Long id) {
        return departmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(DepartmentNotFoundException::new);
    }
}
