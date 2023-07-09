package com.yello.server.global.security;

import java.io.IOException;
import java.util.Map;

import com.yello.server.domain.user.util.AuthTokenProvider;
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
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {

    private AuthTokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        Map<String, String> tokens = tokenProvider.resolveToken((HttpServletRequest) request);

        try {
            if (!CollectionUtils.isEmpty(tokens) && tokenProvider.validateToken(tokens.get(AuthTokenProvider.REFRESH_TOKEN))) {
                Authentication auth = tokenProvider.getAuthentication(tokens, response);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return;
        }
        filterChain.doFilter(request, response);
    }

}
