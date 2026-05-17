package com.project.bangjjack.domain.checklist.domain.repository;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<LifestyleChecklist, Long> {

    Optional<LifestyleChecklist> findByUserAndDeletedFalse(User user);
}
