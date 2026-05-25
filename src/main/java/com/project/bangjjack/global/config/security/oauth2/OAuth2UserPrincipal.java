package com.project.bangjjack.global.config.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;


@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class OAuth2UserPrincipal implements OAuth2User {

    private final String providerId;
    private final String username;
    private final String email;
    private final String picture;

    public static OAuth2UserPrincipal of(String providerId, String username, String email, String picture) {
        return new OAuth2UserPrincipal(providerId, username, email, picture);
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", providerId);
        attributes.put("name", username);
        attributes.put("email", email);
        attributes.put("picture", picture);
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return username;
    }
}
