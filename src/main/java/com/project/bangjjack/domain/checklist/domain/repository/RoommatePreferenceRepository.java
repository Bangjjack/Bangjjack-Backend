package com.project.bangjjack.domain.checklist.domain.repository;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoommatePreferenceRepository extends JpaRepository<RoommatePreference, Long> {

    Optional<RoommatePreference> findByUserAndDeletedFalse(User user);
}
