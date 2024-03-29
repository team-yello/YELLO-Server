package com.yello.server.domain.authorization.filter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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

    public static final String X_ACCESS_AUTH = "X-ACCESS-AUTH";
    public static final String X_REFRESH_AUTH = "X-REFRESH-AUTH";
    public static final String BEARER = "Bearer ";
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        final String requestPath = request.getServletPath();

        if (requestPath.equals("/")
            || requestPath.startsWith("/docs")
            || requestPath.startsWith("/actuator") || requestPath.startsWith("/prometheus")
            || requestPath.startsWith("/api/v1/admin/login")
            || requestPath.startsWith("/api/v1/auth")
            || requestPath.startsWith("/v2/apple/notifications")
            || requestPath.startsWith("/v2/google/notifications")
            || requestPath.startsWith("/api/v1/admob/verify")
            || requestPath.startsWith("/api/v1/statistics")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requestPath.equals("/api/v1/user/post/comment") && request.getHeader(AUTHORIZATION) == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final Long userId = (Long) request.getAttribute("userId");
        log.info("Current user's id: {}", userId);

        final User user = userRepository.getById(userId);

        try {
            val authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getYelloId(),
                null,
                List.of(new SimpleGrantedAuthority("USER"))
            );

            authenticationToken.setDetails(user);

            SecurityContextHolder.getContext()
                .setAuthentication(authenticationToken);

            log.info("[+] Token in SecurityContextHolder");
        } catch (AuthenticationException exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

}
