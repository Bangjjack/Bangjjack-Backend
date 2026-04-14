package com.project.bangjjack.domain.department.domain.repository;

import com.project.bangjjack.domain.department.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByIdAndDeletedFalse(Long id);
}
