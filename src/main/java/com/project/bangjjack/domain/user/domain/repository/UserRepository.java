package com.project.bangjjack.domain.user.domain.repository;

import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderIdAndDeletedFalse(String providerId);

    Optional<User> findByIdAndDeletedFalse(Long id);
}
