package com.yello.server.domain.authorization.filter;

import static com.yello.server.global.common.ErrorCode.AUTH_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.authorization.exception.AuthNotFoundException;
import com.yello.server.domain.user.entity.UserRepository;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String BEARER = "Bearer ";
    public static final String X_ACCESS_AUTH = "X-ACCESS-AUTH";
    public static final String X_REFRESH_AUTH = "X-REFRESH-AUTH";
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        val requestPath = request.getServletPath();

        if (requestPath.equals("/")
            || requestPath.startsWith("/swagger-ui") || requestPath.startsWith("/v3/api-docs")
            || requestPath.startsWith("/actuator") || requestPath.startsWith("/prometheus")
            || requestPath.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        val userId = (Long) request.getAttribute("userId");
        log.info("Current user's id: {}", userId);

        val tokenUser = userRepository.findById(userId)
            .orElseThrow(() -> new AuthNotFoundException(AUTH_NOT_FOUND_USER_EXCEPTION));

        try {
            val authenticationToken = new UsernamePasswordAuthenticationToken(
                tokenUser.getYelloId(),
                null,
                List.of(new SimpleGrantedAuthority("USER"))
            );

            authenticationToken.setDetails(tokenUser);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.info("[+] Token in SecurityContextHolder");
        } catch (AuthenticationException exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

}
