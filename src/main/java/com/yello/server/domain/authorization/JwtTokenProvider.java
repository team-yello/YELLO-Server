package com.yello.server.domain.authorization;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    //TODO 릴리즈 이후 시간 수정
    private static final Long accessTokenValidTime = ofMinutes(1).toMillis();
    private static final Long refreshTokenValidTime = ofDays(14).toMillis();
    public static String ACCESS_TOKEN = "accessToken";
    public static String REFRESH_TOKEN = "refreshToken";
    @Value("${spring.jwt.secret}")
    private String secretKey;

    public Long getUserId(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {

        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();

        String userId = parser.parseClaimsJws(token)
            .getBody()
            .getId();

        return Long.valueOf(userId);
    }

    public String getUserUuid(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean isExpired(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return true;
        }

        return false;
    }

    public boolean isRefreshToken(String token) {
        val header = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return REFRESH_TOKEN.equals(header.get("type").toString());
    }

    public boolean isAccessToken(String token) {
        val header = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return ACCESS_TOKEN.equals(header.get("type").toString());
    }

    public String createAccessToken(Long userId, String uuid) {
        return createJwt(userId, uuid, accessTokenValidTime, ACCESS_TOKEN);
    }

    public String createRefreshToken(Long userId, String uuid) {
        return createJwt(userId, uuid, refreshTokenValidTime, REFRESH_TOKEN);
    }

    public ServiceTokenVO createServiceToken(Long userId, String uuid) {
        return ServiceTokenVO.of(
            createAccessToken(userId, uuid),
            createRefreshToken(userId, uuid)
        );
    }

    public String createJwt(Long userId, String uuid, Long tokenValidTime, String tokenType) {
        Claims claims = Jwts.claims()
            .setSubject(uuid)
            .setId(String.valueOf(userId));

        return Jwts.builder()
            .setClaims(claims)
            .setHeaderParam("type", tokenType)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime))
            .signWith(HS256, secretKey)
            .compact();
    }

    public void tryParse(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();

        parser.parse(token);
    }
}
