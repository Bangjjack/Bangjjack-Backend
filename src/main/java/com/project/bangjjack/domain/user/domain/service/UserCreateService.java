package com.project.bangjjack.domain.user.domain.service;

import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }
}
