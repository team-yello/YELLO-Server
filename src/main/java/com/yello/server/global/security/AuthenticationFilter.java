package com.yello.server.global.security;

import java.io.IOException;
import java.util.Map;

import com.yello.server.domain.auth.dto.ServiceTokenVO;
import com.yello.server.domain.auth.util.AuthTokenProvider;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {

    private AuthTokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ServiceTokenVO tokens = tokenProvider.resolveToken((HttpServletRequest) request);

        try {
            if (tokens.accessToken().isEmpty() || tokens.refreshToken().isEmpty()){
                throw new CustomException(ErrorCode.NO_REQUEST_DATA_EXCEPTION, ErrorCode.REQUEST_VALIDATION_EXCEPTION.getMessage());
            }

            if(!tokenProvider.validateToken(tokens.refreshToken())){
                throw new CustomException(ErrorCode.TOKEN_TIME_EXPIRED_EXCEPTION, ErrorCode.TOKEN_TIME_EXPIRED_EXCEPTION.getMessage());
            }

            Authentication auth = tokenProvider.getAuthentication(tokens, response);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
//            HttpServletResponse res = (HttpServletResponse) response;
//            res.sendError(HttpStatus.UNAUTHORIZED.val리ue(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            System.out.println("e = " + e);
            throw new CustomException(ErrorCode.NOT_SIGNIN_USER_EXCEPTION, ErrorCode.NOT_SIGNIN_USER_EXCEPTION.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
