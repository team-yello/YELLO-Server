package com.yello.server.domain.authorization.filter;

import static com.yello.server.global.common.ErrorCode.INVALID_TOKEN;

import com.yello.server.domain.authorization.exception.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException exception) {
            log.error("[-] Invalid Token");
            throw new InvalidTokenException(INVALID_TOKEN);
        } catch (AuthenticationException exception) {
            log.error(exception.getMessage());
        }
    }
}
