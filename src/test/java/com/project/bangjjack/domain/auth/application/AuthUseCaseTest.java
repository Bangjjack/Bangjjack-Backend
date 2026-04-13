package com.project.bangjjack.domain.auth.application;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.exception.InvalidAuthorizationCodeException;
import com.project.bangjjack.domain.auth.application.usecase.AuthUseCase;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.infrastructure.redis.RedisService;
import com.project.bangjjack.global.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private RedisService redisService;

    @Mock
    private UserGetService userGetService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Nested
    @DisplayName("토큰 교환 시")
    class ExchangeToken {

        @Test
        @DisplayName("유효한 코드로 요청하면 accessToken과 회원 정보가 반환된다")
        void 유효한_코드로_토큰_교환_성공() {
            // given
            String code = "valid-auth-code";
            Long userId = 1L;
            User user = User.create("google-sub-123", "홍길동", "hong@gmail.com", "https://pic.url");
            String expectedToken = "jwt.access.token";

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(String.valueOf(userId));
            given(userGetService.getById(userId)).willReturn(user);
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn(expectedToken);

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.accessToken()).isEqualTo(expectedToken);
            assertThat(response.username()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("만료되거나 존재하지 않는 코드로 요청하면 예외가 발생한다")
        void 만료된_코드로_요청하면_예외_발생() {
            // given
            String expiredCode = "expired-or-invalid-code";
            given(redisService.validateAndConsumeAuthorizationCode(expiredCode)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authUseCase.exchangeToken(new TokenExchangeRequest(expiredCode)))
                    .isInstanceOf(InvalidAuthorizationCodeException.class);
        }

        @Test
        @DisplayName("이미 사용된 코드로 재요청하면 예외가 발생한다")
        void 이미_사용된_코드로_재요청하면_예외_발생() {
            // given
            String usedCode = "already-used-code";
            given(redisService.validateAndConsumeAuthorizationCode(usedCode)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authUseCase.exchangeToken(new TokenExchangeRequest(usedCode)))
                    .isInstanceOf(InvalidAuthorizationCodeException.class);
        }
    }
}
