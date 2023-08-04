package com.yello.server.domain.authorization.filter;

import static com.yello.server.domain.authorization.filter.JwtFilter.BEARER;
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

        val requestPath = request.getServletPath();

        if (requestPath.equals("/")
            || requestPath.startsWith("/swagger-ui") || requestPath.startsWith("/v3/api-docs")
            || requestPath.startsWith("/actuator") || requestPath.startsWith("/prometheus")
            || requestPath.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        val accessHeader = request.getHeader(AUTHORIZATION);
        log.info("Authorization : {}", accessHeader);

        if (accessHeader == null || !accessHeader.startsWith(BEARER)) {
            throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
        }

        val token = accessHeader.substring(BEARER.length());
        try {
            Long userId = jwtTokenProvider.getUserId(token);
            request.setAttribute("userId", userId);
        } catch (ExpiredJwtException e) {
            log.info("토큰이 만료되었습니다. 토큰 재발급 API 호출이 필요합니다.");
            throw new InvalidTokenException(EXPIRED_TOKEN);
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
