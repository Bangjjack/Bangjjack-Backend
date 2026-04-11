package com.project.bangjjack.domain.user.domain.service;

import com.project.bangjjack.domain.user.application.exception.UserNotFoundException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    public User getById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
