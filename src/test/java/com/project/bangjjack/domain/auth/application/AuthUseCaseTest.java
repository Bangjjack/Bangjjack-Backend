package com.project.bangjjack.domain.auth.application;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.exception.InvalidAuthorizationCodeException;
import com.project.bangjjack.domain.auth.application.exception.UnauthorizedEmailDomainException;
import com.project.bangjjack.domain.auth.application.usecase.AuthUseCase;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserCreateService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.config.security.oauth2.OAuthAttributes;
import com.project.bangjjack.global.infrastructure.redis.RedisService;
import com.project.bangjjack.global.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private RedisService redisService;

    @Mock
    private UserGetService userGetService;

    @Mock
    private UserCreateService userCreateService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Nested
    @DisplayName("토큰 교환 시")
    class ExchangeToken {

        @Test
        @DisplayName("가천대학교 이메일 기존 사용자가 유효한 코드로 요청하면 accessToken과 회원 정보가 반환된다")
        void 가천대학교_이메일_기존_사용자_토큰_교환_성공() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            User user = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            String expectedToken = "jwt.access.token";

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(user));
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn(expectedToken);

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.accessToken()).isEqualTo(expectedToken);
            assertThat(response.username()).isEqualTo("홍길동");
            verify(userCreateService, never()).createUser(any());
        }

        @Test
        @DisplayName("가천대학교 이메일 신규 사용자가 토큰 교환 시 자동 가입된다")
        void 가천대학교_이메일_신규_사용자_자동_가입() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-999", "신규자", "new@gachon.ac.kr", "https://pic.url");
            User createdUser = User.create("google-sub-999", "신규자", "new@gachon.ac.kr", "https://pic.url");

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-999")).willReturn(Optional.empty());
            given(userCreateService.createUser(any(User.class))).willReturn(createdUser);
            given(jwtProvider.createMemberAccessToken(createdUser.getId(), createdUser.getUsername())).willReturn("jwt.access.token");

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.username()).isEqualTo("신규자");
            verify(userCreateService).createUser(any(User.class));
        }

        @Test
        @DisplayName("온보딩 미완료 회원이 토큰을 교환하면 isOnboarded=false가 반환된다")
        void 온보딩_미완료_회원의_isOnboarded는_false() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            User user = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(user));
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn("jwt.access.token");

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.isOnboarded()).isFalse();
        }

        @Test
        @DisplayName("온보딩 완료 회원이 토큰을 교환하면 isOnboarded=true가 반환된다")
        void 온보딩_완료_회원의_isOnboarded는_true() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            User user = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(user));
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn("jwt.access.token");

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.isOnboarded()).isTrue();
        }

        @Test
        @DisplayName("체크리스트/선호도 미등록 회원이 토큰을 교환하면 두 플래그 모두 false가 반환된다")
        void 체크리스트_선호도_미등록_회원의_플래그는_false() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            User user = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(user));
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn("jwt.access.token");

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.isChecklistRegistered()).isFalse();
            assertThat(response.isRoommatePreferenceRegistered()).isFalse();
        }

        @Test
        @DisplayName("체크리스트만 등록한 회원이 토큰을 교환하면 isChecklistRegistered=true, isRoommatePreferenceRegistered=false가 반환된다")
        void 체크리스트만_등록한_회원의_플래그() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            User user = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            user.completeChecklistRegistration();

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(user));
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn("jwt.access.token");

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.isChecklistRegistered()).isTrue();
            assertThat(response.isRoommatePreferenceRegistered()).isFalse();
        }

        @Test
        @DisplayName("체크리스트+선호도 모두 등록한 회원이 토큰을 교환하면 두 플래그 모두 true가 반환된다")
        void 체크리스트_선호도_모두_등록한_회원의_플래그는_true() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            User user = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");
            user.completeChecklistRegistration();
            user.completeRoommatePreferenceRegistration();

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);
            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(user));
            given(jwtProvider.createMemberAccessToken(user.getId(), user.getUsername())).willReturn("jwt.access.token");

            // when
            TokenExchangeResponse response = authUseCase.exchangeToken(new TokenExchangeRequest(code));

            // then
            assertThat(response.isChecklistRegistered()).isTrue();
            assertThat(response.isRoommatePreferenceRegistered()).isTrue();
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

        @Test
        @DisplayName("가천대학교 이메일이 아닌 경우 UnauthorizedEmailDomainException이 발생하고 User는 생성되지 않는다")
        void 비가천_이메일_토큰_교환_시_예외() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-456", "김철수", "kim@gmail.com", "https://pic.url");

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);

            // when & then
            assertThatThrownBy(() -> authUseCase.exchangeToken(new TokenExchangeRequest(code)))
                    .isInstanceOf(UnauthorizedEmailDomainException.class);

            verify(userGetService, never()).findByProviderId(any());
            verify(userCreateService, never()).createUser(any());
            verify(jwtProvider, never()).createMemberAccessToken(any(), any());
        }

        @Test
        @DisplayName("타 대학(.ac.kr이지만 gachon이 아닌) 이메일로 토큰 교환 시 예외가 발생한다")
        void 타학교_이메일_토큰_교환_시_예외() {
            // given
            String code = "valid-auth-code";
            OAuthAttributes attributes = OAuthAttributes.of("google-sub-789", "이영희", "lee@korea.ac.kr", "https://pic.url");

            given(redisService.validateAndConsumeAuthorizationCode(code)).willReturn(attributes);

            // when & then
            assertThatThrownBy(() -> authUseCase.exchangeToken(new TokenExchangeRequest(code)))
                    .isInstanceOf(UnauthorizedEmailDomainException.class);

            verify(userGetService, never()).findByProviderId(any());
            verify(userCreateService, never()).createUser(any());
        }
    }
}
