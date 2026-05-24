package com.project.bangjjack.domain.user.domain.service;

import com.project.bangjjack.domain.user.application.exception.UserNotFoundException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderIdAndDeletedFalse(providerId);
    }

    public User getById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public Map<Long, User> getByIds(Collection<Long> ids) {
        return userRepository.findAllByIdInAndDeletedFalse(ids)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));
    }
}
