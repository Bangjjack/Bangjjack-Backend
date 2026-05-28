package com.project.bangjjack.domain.user.domain.repository;

import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderIdAndDeletedFalse(String providerId);

    Optional<User> findByIdAndDeletedFalse(Long id);

    List<User> findAllByIdInAndDeletedFalse(Collection<Long> ids);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.department
            WHERE u.id <> :requesterId
              AND u.deleted = false
              AND u.gender = :gender
              AND u.campus = :campus
              AND u.dormitory = :dormitory
              AND u.isChecklistRegistered = true
              AND u.isRoommatePreferenceRegistered = true
            ORDER BY u.id ASC
            """)
    List<User> findRecommendationCandidates(
            @Param("requesterId") Long requesterId,
            @Param("gender") Gender gender,
            @Param("campus") Campus campus,
            @Param("dormitory") Dormitory dormitory
    );
}
