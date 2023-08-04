package com.yello.server.domain.authorization;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofSeconds;

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

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    private static final Long ACCESS_TOKEN_VALID_TIME = ofSeconds(30).toMillis();
    private static final Long REFRESH_TOKEN_VALID_TIME = ofDays(14).toMillis();

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public Long getUserId(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException,
        IllegalArgumentException {

        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();

        String userId = parser.parseClaimsJws(token)
            .getBody()
            .getId();

        return Long.valueOf(userId);
    }

    public String getUserUuid(String token)
        throws ExpiredJwtException, MalformedJwtException, SignatureException,
        IllegalArgumentException {
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
        return createJwt(userId, uuid, ACCESS_TOKEN_VALID_TIME, ACCESS_TOKEN);
    }

    public String createRefreshToken(Long userId, String uuid) {
        return createJwt(userId, uuid, REFRESH_TOKEN_VALID_TIME, REFRESH_TOKEN);
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
        throws ExpiredJwtException, MalformedJwtException, SignatureException,
        IllegalArgumentException {
        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build();

        parser.parse(token);
    }
}
