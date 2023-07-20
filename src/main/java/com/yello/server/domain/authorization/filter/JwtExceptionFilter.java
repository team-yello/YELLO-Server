package com.yello.server.domain.authorization.filter;

import static com.yello.server.domain.authorization.JwtTokenProvider.ACCESS_TOKEN;
import static com.yello.server.domain.authorization.JwtTokenProvider.REFRESH_TOKEN;
import static com.yello.server.domain.authorization.filter.JwtFilter.BEARER;
import static com.yello.server.domain.authorization.filter.JwtFilter.X_ACCESS_AUTH;
import static com.yello.server.domain.authorization.filter.JwtFilter.X_REFRESH_AUTH;
import static com.yello.server.global.common.ErrorCode.AUTHENTICATION_ERROR;
import static com.yello.server.global.common.ErrorCode.EXPIRED_TOKEN;
import static com.yello.server.global.common.ErrorCode.ILLEGAL_TOKEN;
import static com.yello.server.global.common.ErrorCode.MALFORMED_TOKEN;
import static com.yello.server.global.common.ErrorCode.SIGNATURE_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import com.yello.server.domain.authorization.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();

        if (requestPath.equals("/")
            || requestPath.startsWith("/swagger-ui")
            || requestPath.startsWith("/v3/api-docs")
            || requestPath.startsWith("/api/v1/auth/oauth")
            || requestPath.startsWith("/api/v1/auth/signup")
            || requestPath.startsWith("/api/v1/auth/valid")
            || requestPath.startsWith("/api/v1/auth/friend")
            || requestPath.startsWith("/api/v1/auth/school/school")
            || requestPath.startsWith("/api/v1/auth/school/department")) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> tokens = new HashMap<>();

        if (requestPath.startsWith("/api/v1/auth/token/issue")) {
            val accessHeader = request.getHeader(X_ACCESS_AUTH);
            val refreshHeader = request.getHeader(X_REFRESH_AUTH);
            log.info("X_ACCESS_AUTH : {}", accessHeader);
            log.info("X_REFRESH_AUTH : {}", refreshHeader);

            if (accessHeader==null || !accessHeader.startsWith(BEARER)
                || refreshHeader==null || !refreshHeader.startsWith(BEARER)) {
                throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
            }

            tokens.put(ACCESS_TOKEN, accessHeader.substring(BEARER.length()));
            tokens.put(REFRESH_TOKEN, refreshHeader.substring(BEARER.length()));
        } else {
            val accessHeader = request.getHeader(AUTHORIZATION);
            log.info("Authorization : {}", accessHeader);

            if (accessHeader==null || !accessHeader.startsWith(BEARER)) {
                throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
            }

            tokens.put(ACCESS_TOKEN, accessHeader.substring(BEARER.length()));
        }

        try {
            if (requestPath.startsWith("/api/v1/auth/token/issue")) {
                tokens.forEach(request::setAttribute);
                tokens.forEach((key, value) -> jwtTokenProvider.tryParse(value));
            } else {
                Long userId = jwtTokenProvider.getUserId(tokens.get(ACCESS_TOKEN));
                request.setAttribute("userId", userId);
            }
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다. 토큰 재발급 API 호출이 필요합니다.");

            if (!requestPath.startsWith("/api/v1/auth/token/issue")) {
                throw new InvalidTokenException(EXPIRED_TOKEN);
            }
        } catch (MalformedJwtException e) {
            log.info("토큰이 변조되었습니다.");
            throw new InvalidTokenException(MALFORMED_TOKEN);
        } catch (SignatureException e) {
            log.info("토큰의 서명이 이상합니다.");
            throw new InvalidTokenException(SIGNATURE_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("토큰이 null이거나 이상한 값입니다.");
            throw new InvalidTokenException(ILLEGAL_TOKEN);
        }

        filterChain.doFilter(request, response);
    }
}
