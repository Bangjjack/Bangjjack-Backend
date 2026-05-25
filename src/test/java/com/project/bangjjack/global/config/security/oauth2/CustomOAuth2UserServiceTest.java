package com.project.bangjjack.global.config.security.oauth2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Nested
    @DisplayName("Google OAuth2 사용자 처리 시")
    class ProcessOAuth2User {

        @Test
        @DisplayName("Google attributes로부터 OAuth2UserPrincipal이 생성된다 (DB 저장 없음)")
        void OAuth2UserPrincipal_생성_성공() {
            // given
            Map<String, Object> attributes = Map.of(
                    "sub", "google-sub-123",
                    "name", "홍길동",
                    "email", "hong@gachon.ac.kr",
                    "picture", "https://pic.url"
            );

            // when
            OAuth2User result = customOAuth2UserService.processOAuth2User(attributes);

            // then
            assertThat(result).isInstanceOf(OAuth2UserPrincipal.class);
            OAuth2UserPrincipal principal = (OAuth2UserPrincipal) result;
            assertThat(principal.getProviderId()).isEqualTo("google-sub-123");
            assertThat(principal.getUsername()).isEqualTo("홍길동");
            assertThat(principal.getEmail()).isEqualTo("hong@gachon.ac.kr");
            assertThat(principal.getPicture()).isEqualTo("https://pic.url");
        }

        @Test
        @DisplayName("가천대학교 이메일이 아닌 경우에도 OAuth2 단계에서는 예외를 던지지 않는다 (검증은 토큰 교환 단계에서 수행)")
        void 비가천_이메일도_OAuth2_단계에서는_통과() {
            // given
            Map<String, Object> attributes = Map.of(
                    "sub", "google-sub-456",
                    "name", "김철수",
                    "email", "kim@gmail.com",
                    "picture", "https://pic.url"
            );

            // when
            OAuth2User result = customOAuth2UserService.processOAuth2User(attributes);

            // then
            assertThat(result).isInstanceOf(OAuth2UserPrincipal.class);
            assertThat(((OAuth2UserPrincipal) result).getEmail()).isEqualTo("kim@gmail.com");
        }
    }
}
