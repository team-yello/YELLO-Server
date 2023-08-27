package com.yello.server.domain.authorization.filter;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        final String requestPath = request.getServletPath();

        if (requestPath.equals("/")
            || requestPath.startsWith("/docs")
            || requestPath.startsWith("/actuator") || requestPath.startsWith("/prometheus")
            || requestPath.startsWith("/api/v1/auth")) {
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
