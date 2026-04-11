package com.project.bangjjack.global.config.security.oauth2;

import com.project.bangjjack.global.jwt.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;


@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class OAuth2UserPrincipal implements OAuth2User {

    private final Long memberId;
    private final String memberName;
    private final Role role;
    private final Map<String, Object> oAuth2Attributes;

    public static OAuth2UserPrincipal of(Long memberId, String memberName, Map<String, Object> attributes) {
        return new OAuth2UserPrincipal(memberId, memberName, Role.MEMBER, attributes);
    }

    public Map<String, Object> getAttributes() {
        return oAuth2Attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        return memberName;
    }
}
