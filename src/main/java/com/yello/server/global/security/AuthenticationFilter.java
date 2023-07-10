package com.yello.server.global.security;

import java.io.IOException;
import java.util.*;

import com.yello.server.domain.auth.dto.ServiceTokenVO;
import com.yello.server.domain.auth.util.AuthTokenProvider;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private String[] NON_AUTH_ALL_LIST = {
            "/api/v1/auth/oauth",
            "/api/v1/auth/univ",
            "/swagger-ui"
    };
    private String[] NON_AUTH_LIST = {
            "/api/v1/auth/valid",
            "/data"
    };

    private final AuthTokenProvider tokenProvider;
    private final UserRepository userRepository;

    private boolean isNonAuthURI(String uri) {
        for(String authAllUri : NON_AUTH_ALL_LIST){
            if(uri.startsWith(authAllUri)) {
                return true;
            }
        }

        for(String authUri : NON_AUTH_LIST) {
            if(uri.equals(authUri)){
                return true;
            }
        }

        if(uri.endsWith(".html") || uri.equals("/")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!isNonAuthURI(request.getRequestURI())){
            ServiceTokenVO tokens = tokenProvider.resolveToken(request);

            if (Objects.isNull(tokens.accessToken()) || Objects.isNull(tokens.refreshToken())){
                throw new CustomException(ErrorCode.NO_REQUEST_DATA_EXCEPTION, ErrorCode.NO_REQUEST_DATA_EXCEPTION.getMessage());
            }

            try {
                tokenProvider.parseAccessToken(tokens.accessToken());
                tokenProvider.parseAccessToken(tokens.refreshToken());
            } catch (ExpiredJwtException e){
                throw new CustomException(ErrorCode.TOKEN_EXPIRED_EXCEPTION, ErrorCode.TOKEN_EXPIRED_EXCEPTION.getMessage());
            } catch (MalformedJwtException e){
                throw new CustomException(ErrorCode.TOKEN_MALFORMED_EXCEPTION, ErrorCode.TOKEN_MALFORMED_EXCEPTION.getMessage());
            } catch (SignatureException e){
                throw new CustomException(ErrorCode.TOKEN_SIGNATURE_EXCEPTION, ErrorCode.TOKEN_SIGNATURE_EXCEPTION.getMessage());
            }

            AuthTokenProvider.TokenInfo tokenInfo = tokenProvider.parseAccessToken(tokens.accessToken());
            userRepository.findById(tokenInfo.id())
                    .orElseThrow(() -> {throw new CustomException(ErrorCode.NOT_SIGNIN_USER_EXCEPTION, ErrorCode.NOT_SIGNIN_USER_EXCEPTION.getMessage());});

            try {
                Authentication auth = tokenProvider.getAuthentication(tokens, response);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                System.out.println("e = " + e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
