package com.project.bangjjack.global.dev;

import com.project.bangjjack.domain.auth.application.dto.request.UserIdTokenIssueRequest;
import com.project.bangjjack.domain.auth.application.dto.response.UserIdTokenIssueResponse;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile({"local", "dev"})
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevUseCase {

    private final UserGetService userGetService;
    private final JwtProvider jwtProvider;

    public UserIdTokenIssueResponse issueTokenByUserId(UserIdTokenIssueRequest request) {
        User user = userGetService.getById(request.userId());
        String accessToken = jwtProvider.createMemberAccessToken(user.getId(), user.getUsername());
        return UserIdTokenIssueResponse.of(accessToken, user.getId(), user.getUsername());
    }
}
