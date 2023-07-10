package com.yello.server.domain.auth.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.yello.server.domain.auth.dto.ServiceTokenVO;
import com.yello.server.global.security.AccessUserService;
import com.yello.server.global.security.AppUser;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenProvider {
    private final static String AccessTokenHeader = "X-AUTH-ACCESS";
    private final static String RefreshTokenHeader = "X-AUTH-REFRESH";

    @Value("${spring.oauth.token.secret}")
    private String secretKey;

    private final AccessUserService userDetailsService;

    @Builder
    public record TokenInfo(
            long id,
            String uuid
    ) {}

    private Date createExpirationDate(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);

        return calendar.getTime();
    }

    public ServiceTokenVO createToken(long userId, String uuid) {
        Date today = new Date();
        Claims claims = Jwts.claims().setSubject(uuid).setId(String.valueOf(userId));

        // 릴리즈 시 30분으로 수정할 것
        Date accessTokenExpiredDate = createExpirationDate(Calendar.DATE, 1);
        Date refreshTokenExpiredDate = createExpirationDate(Calendar.DATE, 30);

        final String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(today)
                .setExpiration(accessTokenExpiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        final String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(today)
                .setExpiration(refreshTokenExpiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return ServiceTokenVO.of(accessToken, refreshToken);
    }

    public Authentication getAuthentication(ServiceTokenVO tokens, ServletResponse response) throws IOException {
        Claims claims = null;
        boolean requireNewToken = false;

        if (StringUtils.hasText(tokens.accessToken()) && StringUtils.hasText(tokens.refreshToken())) {
            try {
                claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(tokens.accessToken())
                        .getBody();
            } catch (ExpiredJwtException e) {
                claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(tokens.refreshToken())
                        .getBody();
                requireNewToken = true;
            }
        }

        if (null == claims) {
            log.error("The claim [{}] is invalid", claims);
            throw new AccessDeniedException("can't access what you requested.");
        }

        // accessToken이 만료가 된 경우에는 accessToken 및 refreshToken을 업데이트한다.
        if (requireNewToken) {
            log.info("The access token has been expired");

            ServiceTokenVO newTokens = createToken(Long.parseLong(claims.getId()), claims.getSubject());

            boolean isUpdated = userDetailsService.updateTokens(
                    claims.getSubject(), tokens.refreshToken(), newTokens.accessToken(), newTokens.refreshToken());

            HttpServletResponse res = (HttpServletResponse) response;

            if (isUpdated) {
                res.addHeader(AccessTokenHeader, newTokens.accessToken());
                res.addHeader(RefreshTokenHeader, newTokens.refreshToken());
            }
            else {
                // 업데이트가 실패했을 경우에는 RefreshToken이 변경된 걸로 판단하고 에러를 반환한다.
                log.error("Failed to re-create tokens");
                res.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                return null;
            }
        }

        // Basic Authorization은 ID값이 없으므로
        if (StringUtils.hasText(claims.getId())) {
            UserDetails userDetails = userDetailsService.loadByAccountId(Long.parseLong(claims.getId()));
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
            AppUser appUser = new AppUser(null);
            return new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
        }
    }

    public ServiceTokenVO resolveToken(HttpServletRequest req) {
        final String accessToken = req.getHeader(AccessTokenHeader);
        final String refreshToken = req.getHeader(RefreshTokenHeader);

        return ServiceTokenVO.of(accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("The given token[{}] has been expired", token);
            return false;
        } catch (Exception e) {
            log.error("The given token[{}] is not valid - {}", token, e.getMessage());
            return false;
        }
    }

    public TokenInfo parseAccessToken(String accessToken) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
        return TokenInfo.builder().id(Long.parseLong(claims.getId())).uuid(claims.getSubject()).build();
    }
}
