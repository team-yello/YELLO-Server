package com.yello.server.domain.authorization.service;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.time.Duration;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 해당 객체는 secretKey를 제외한 State를 가지지 않도록 합니다.
 */
@Log4j2
@Component
public class TokenJwtProvider implements TokenProvider {

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    public String secretKey;

    public TokenJwtProvider(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public Long getUserId(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException,
        IllegalArgumentException {

        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey.getBytes())
            .build();

        String userId = parser.parseClaimsJws(token)
            .getBody()
            .getId();

        return Long.valueOf(userId);
    }

    @Override
    public String getUserUuid(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException,
        IllegalArgumentException {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    @Override
    public boolean isExpired(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return true;
        }

        return false;
    }

    @Override
    public String createAccessToken(Long userId, String uuid, Duration duration) {
        return createJwt(userId, uuid, duration, ACCESS_TOKEN);
    }

    @Override
    public String createRefreshToken(Long userId, String uuid, Duration duration) {
        return createJwt(userId, uuid, duration, REFRESH_TOKEN);
    }

    private String createJwt(Long userId, String uuid, Duration duration, String tokenType) {
        Claims claims = Jwts.claims()
            .setSubject(uuid)
            .setId(String.valueOf(userId));

        return Jwts.builder()
            .setClaims(claims)
            .setHeaderParam("type", tokenType)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + duration.toMillis()))
            .signWith(HS256, secretKey.getBytes())
            .compact();
    }
}
