package com.yello.server.domain.authorization.filter;

import static com.yello.server.domain.authorization.filter.JwtFilter.BEARER;
import static com.yello.server.domain.authorization.filter.JwtFilter.X_ACCESS_AUTH;
import static com.yello.server.domain.authorization.filter.JwtFilter.X_REFRESH_AUTH;
import static com.yello.server.global.common.ErrorCode.AUTHENTICATION_ERROR;
import static com.yello.server.global.common.ErrorCode.EXPIRED_TOKEN;
import static com.yello.server.global.common.ErrorCode.ILLEGAL_TOKEN;
import static com.yello.server.global.common.ErrorCode.MALFORMED_TOKEN;
import static com.yello.server.global.common.ErrorCode.SIGNATURE_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import com.yello.server.domain.authorization.exception.InvalidTokenException;
import com.yello.server.domain.authorization.service.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        val requestPath = request.getServletPath();

        if (requestPath.equals("/")
            || requestPath.startsWith("/docs")
            || requestPath.startsWith("/actuator") || requestPath.startsWith("/prometheus")
            || requestPath.startsWith("/api/v1/admin/login")
            || requestPath.startsWith("/v2/apple/notifications")
            || requestPath.startsWith("/v2/google/notifications")
            || (requestPath.startsWith("/api/v1/auth")
            && !requestPath.startsWith("/api/v1/auth/token/issue"))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (requestPath.startsWith("/api/v1/auth/token/issue")) {
                val accessHeader = request.getHeader(X_ACCESS_AUTH);
                val refreshHeader = request.getHeader(X_REFRESH_AUTH);
                log.info("Authorization-access : {}", accessHeader);
                log.info("Authorization-refresh : {}", refreshHeader);

                if (accessHeader == null || !accessHeader.startsWith(BEARER)
                    || refreshHeader == null || !refreshHeader.startsWith(BEARER)) {
                    throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
                }

                val token = accessHeader.substring(BEARER.length());
                Long userId = tokenProvider.getUserId(token);
                request.setAttribute("userId", userId);
            } else {
                val accessHeader = request.getHeader(AUTHORIZATION);
                log.info("Authorization : {}", accessHeader);

                if (accessHeader == null || !accessHeader.startsWith(BEARER)) {
                    throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
                }

                val token = accessHeader.substring(BEARER.length());
                Long userId = tokenProvider.getUserId(token);
                request.setAttribute("userId", userId);
            }
        } catch (ExpiredJwtException e) {
            if (!requestPath.startsWith("/api/v1/auth/token/issue")) {
                log.info("토큰이 만료되었습니다. 토큰 재발급 API 호출이 필요합니다.");
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
