package com.project.bangjjack.global.config.security.oauth2;

import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserCreateService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String ALLOWED_EMAIL_DOMAIN = "@gachon.ac.kr";

    private final UserGetService userGetService;
    private final UserCreateService userCreateService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(oAuth2User.getAttributes());
    }

    OAuth2User processOAuth2User(Map<String, Object> attributes) {
        String providerId = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String picture = (String) attributes.get("picture");

        if (!email.endsWith(ALLOWED_EMAIL_DOMAIN)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("unauthorized_email_domain",
                            "가천대학교 이메일(" + ALLOWED_EMAIL_DOMAIN + ")로만 로그인 가능합니다.", null)
            );
        }

        User user = userGetService.findByProviderId(providerId)
                .orElseGet(() -> userCreateService.createUser(
                        User.create(providerId, name, email, picture)
                ));

        return OAuth2UserPrincipal.of(user.getId(), user.getUsername(), attributes);
    }
}
