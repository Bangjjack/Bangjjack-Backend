package com.project.bangjjack.global.annotation;

import com.project.bangjjack.global.common.exception.UnAuthorizedException;
import com.project.bangjjack.global.config.security.oauth2.OAuth2UserPrincipal;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentMemberId.class);
        boolean isLongType = Long.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && isLongType;
    }

    @Override
    public Long resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) {
        CurrentMemberId annotation = parameter.getParameterAnnotation(CurrentMemberId.class);
        if (annotation == null) {
            return null;
        }

        boolean required = annotation.required();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            if (required) throw new UnAuthorizedException();
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof MemberPrincipal memberPrincipal) {
            return memberPrincipal.getMemberId();
        }
        if (principal instanceof OAuth2UserPrincipal oAuth2UserPrincipal) {
            return oAuth2UserPrincipal.getMemberId();
        }

        if (required) throw new UnAuthorizedException();
        return null;
    }
}
