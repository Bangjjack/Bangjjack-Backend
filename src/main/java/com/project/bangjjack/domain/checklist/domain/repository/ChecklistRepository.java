package com.project.bangjjack.domain.checklist.domain.repository;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistRepository extends JpaRepository<LifestyleChecklist, Long> {
}
