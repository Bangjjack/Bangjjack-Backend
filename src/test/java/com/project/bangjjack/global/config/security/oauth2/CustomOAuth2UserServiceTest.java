package com.project.bangjjack.global.config.security.oauth2;

import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserCreateService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private UserCreateService userCreateService;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Nested
    @DisplayName("Google OAuth2 사용자 처리 시")
    class ProcessOAuth2User {

        @Test
        @DisplayName("가천대학교 이메일(@gachon.ac.kr)로 신규 로그인 시 사용자가 자동 생성된다")
        void 가천대학교_이메일_신규_로그인_성공() {
            // given
            Map<String, Object> attributes = Map.of(
                    "sub", "google-sub-123",
                    "name", "홍길동",
                    "email", "hong@gachon.ac.kr",
                    "picture", "https://pic.url"
            );
            User createdUser = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");

            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.empty());
            given(userCreateService.createUser(any(User.class))).willReturn(createdUser);

            // when
            OAuth2User result = customOAuth2UserService.processOAuth2User(attributes);

            // then
            assertThat(result).isInstanceOf(OAuth2UserPrincipal.class);
            OAuth2UserPrincipal principal = (OAuth2UserPrincipal) result;
            assertThat(principal.getMemberName()).isEqualTo("홍길동");
            verify(userCreateService).createUser(any(User.class));
        }

        @Test
        @DisplayName("가천대학교 이메일(@gachon.ac.kr)로 기존 사용자 재로그인 시 기존 사용자가 반환된다")
        void 가천대학교_이메일_기존_사용자_재로그인_성공() {
            // given
            Map<String, Object> attributes = Map.of(
                    "sub", "google-sub-123",
                    "name", "홍길동",
                    "email", "hong@gachon.ac.kr",
                    "picture", "https://pic.url"
            );
            User existingUser = User.create("google-sub-123", "홍길동", "hong@gachon.ac.kr", "https://pic.url");

            given(userGetService.findByProviderId("google-sub-123")).willReturn(Optional.of(existingUser));

            // when
            OAuth2User result = customOAuth2UserService.processOAuth2User(attributes);

            // then
            assertThat(result).isInstanceOf(OAuth2UserPrincipal.class);
            verify(userCreateService, never()).createUser(any(User.class));
        }

        @Test
        @DisplayName("가천대학교 이메일이 아닌 계정으로 로그인 시 unauthorized_email_domain 예외가 발생한다")
        void 가천대학교_이메일_아닌_경우_예외_발생() {
            // given
            Map<String, Object> attributes = Map.of(
                    "sub", "google-sub-456",
                    "name", "김철수",
                    "email", "kim@gmail.com",
                    "picture", "https://pic.url"
            );

            // when & then
            assertThatThrownBy(() -> customOAuth2UserService.processOAuth2User(attributes))
                    .isInstanceOf(OAuth2AuthenticationException.class)
                    .satisfies(ex -> {
                        OAuth2AuthenticationException oAuth2Ex = (OAuth2AuthenticationException) ex;
                        assertThat(oAuth2Ex.getError().getErrorCode()).isEqualTo("unauthorized_email_domain");
                    });

            verify(userGetService, never()).findByProviderId(any());
            verify(userCreateService, never()).createUser(any());
        }

        @Test
        @DisplayName("타 학교 이메일(.ac.kr이지만 gachon이 아닌 경우)로 로그인 시 예외가 발생한다")
        void 타학교_이메일_로그인_예외_발생() {
            // given
            Map<String, Object> attributes = Map.of(
                    "sub", "google-sub-789",
                    "name", "이영희",
                    "email", "lee@korea.ac.kr",
                    "picture", "https://pic.url"
            );

            // when & then
            assertThatThrownBy(() -> customOAuth2UserService.processOAuth2User(attributes))
                    .isInstanceOf(OAuth2AuthenticationException.class)
                    .satisfies(ex -> {
                        OAuth2AuthenticationException oAuth2Ex = (OAuth2AuthenticationException) ex;
                        assertThat(oAuth2Ex.getError().getErrorCode()).isEqualTo("unauthorized_email_domain");
                    });
        }
    }
}
