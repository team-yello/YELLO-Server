package com.yello.server.domain.user.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.yello.server.global.exception.ConflictException;
import com.yello.server.global.security.AccessUserService;
import com.yello.server.global.security.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
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

import static com.yello.server.global.common.ErrorCode.TOKEN_TIME_EXPIRED_EXCEPTION;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenProvider {

//    public final static String BASIC_AUTHORIZATION_TOKEN = "authorization";

    public final static String ACCESS_TOKEN = "accessToken";
    public final static String REFRESH_TOKEN = "refreshToken";

//    private final static String Bearer = "Bearer ";

//    private final static String ROLES = "ROLES";

//    private final static String AuthorizationHeader = "Authorization";

    private final static String AccessTokenHeader = "X-AUTH-ACCESS";
    private final static String RefreshTokenHeader = "X-AUTH-REFRESH";

    @Value("${spring.oauth.token.secret}")
    private String secretKey;

    private final AccessUserService userDetailsService;

    @Builder
    public record TokenInfo(long id, String uuid) {}

    private Date createExpirationDate(int field, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.add(field, amount);

        return cal.getTime();
    }

    public Map<String, String> createToken(long userId, String uuid) {
        Date today = new Date();
        Claims claims = Jwts.claims().setSubject(uuid).setId(String.valueOf(userId));
        Date accessTokenExpiredDate = createExpirationDate(Calendar.DATE, 1);
        Date refreshTokenExpiredDate = createExpirationDate(Calendar.DATE, 30);

        final String refreshToken = Jwts.builder().setClaims(claims).setIssuedAt(today)
                .setExpiration(refreshTokenExpiredDate).signWith(SignatureAlgorithm.HS256, secretKey).compact();

        final String accessToken = Jwts.builder().setClaims(claims).setIssuedAt(today)
                .setExpiration(accessTokenExpiredDate).signWith(SignatureAlgorithm.HS256, secretKey).compact();

        return new ImmutableMap.Builder<String, String>().put(ACCESS_TOKEN, accessToken)
                .put(REFRESH_TOKEN, refreshToken).build();
    }

    public Authentication getAuthentication(Map<String, String> tokens, ServletResponse response) throws IOException {
//        final String authorizationToken = tokens.get(BASIC_AUTHORIZATION_TOKEN);
        final String accessToken = tokens.get(ACCESS_TOKEN);
        final String refreshToken = tokens.get(REFRESH_TOKEN);

        Claims claims = null;
        boolean requireNewToken = false;

        if (StringUtils.hasText(accessToken) && StringUtils.hasText(refreshToken)) {
            try {
                claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
            } catch (ExpiredJwtException e) {
                claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody();
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

            Map<String, String> newTokens = createToken(Long.parseLong(claims.getId()), claims.getSubject());
            final String newAccessToken = newTokens.get(ACCESS_TOKEN);
            final String newRefreshToken = newTokens.get(REFRESH_TOKEN);

            boolean isUpdated = userDetailsService.updateTokens(claims.getSubject(), refreshToken, newAccessToken, newRefreshToken);

            HttpServletResponse res = (HttpServletResponse) response;

            if (isUpdated) {
                res.addHeader("x-auth-access", newAccessToken);
                res.addHeader("x-auth-refresh", newRefreshToken);
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

    public Map<String, String> resolveToken(HttpServletRequest req) {
//        final String authorization = req.getHeader(AuthorizationHeader);
        final String accessToken = req.getHeader(AccessTokenHeader);
        final String refreshToken = req.getHeader(RefreshTokenHeader);
        ImmutableMap.Builder<String, String> tokens = new ImmutableMap.Builder<>();

//        if (StringUtils.hasText(authorization) && authorization.startsWith(Bearer)) {
//            final String token = authorization.substring(Bearer.length());
//            if (!authorizationToken.equalsIgnoreCase(token)) {
//                return tokens.build();
//            }
//            tokens.put(BASIC_AUTHORIZATION_TOKEN, token);
//        }
        if (StringUtils.hasText(accessToken)) {
            tokens.put(ACCESS_TOKEN, accessToken);
        }
        if (StringUtils.hasText(refreshToken)) {
            tokens.put(REFRESH_TOKEN, refreshToken);
        }
        return tokens.build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("The given token[{}] has been expired", token);
            throw new ConflictException(TOKEN_TIME_EXPIRED_EXCEPTION, "토큰이 만료되었습니다");
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
