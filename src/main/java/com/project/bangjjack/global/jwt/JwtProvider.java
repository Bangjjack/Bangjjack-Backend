package com.project.bangjjack.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey accessKey;
    private final long expirationSeconds;

    public JwtProvider(
            @Value("${jwt.access.secret}") String accessSecret,
            @Value("${jwt.access.expiration-seconds}") long expirationSeconds
    ) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.expirationSeconds = expirationSeconds;
    }

    public String createMemberAccessToken(Long memberId, String memberName) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("name", memberName)
                .claim("role", Role.MEMBER.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationSeconds * 1000L))
                .signWith(accessKey)
                .compact();
    }
}
