package com.yello.server.domain.authorization.filter;

import static com.yello.server.global.common.ErrorCode.AUTHENTICATION_ERROR;
import static com.yello.server.global.common.ErrorCode.AUTH_NOT_FOUND_USER_EXCEPTION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.exception.AuthNotFoundException;
import com.yello.server.domain.authorization.exception.CustomAuthenticationException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import io.jsonwebtoken.JwtException;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final static String BEARER = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;
    private final String secretKey;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
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

        val authorization = request.getHeader(AUTHORIZATION);
        log.info("Authorization : {}", authorization);

        if (authorization==null || !authorization.startsWith(BEARER)) {
            throw new CustomAuthenticationException(AUTHENTICATION_ERROR);
        }

        String token = authorization.substring(BEARER.length());

        if (jwtTokenProvider.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtTokenProvider.getUserId(token, secretKey);
        log.info("Current user's id: {}", userId);

        // 토큰 재발급일 경우 리프레쉬 토큰 확인
        // 위에서 만료됐는지 확인했기 때문에 따로 만료확인 필요 없음
        // 리프레쉬 토큰이 유효한지와 path 정보를 통해 확인이 끝났기 때문에 컨트롤러에서는 바로 토큰 재발행해주고 보내주면 됨
        if (!((requestPath.startsWith("/api/v1/auth/token") && jwtTokenProvider.isRefreshToken(token, secretKey))
            || jwtTokenProvider.isAccessToken(token, secretKey))
        ) {
            throw new JwtException("");
        }

        User tokenUser = userRepository.findById(userId)
                .orElseThrow(() -> new AuthNotFoundException(AUTH_NOT_FOUND_USER_EXCEPTION));
        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenUser.getYelloId(), null,
            List.of(new SimpleGrantedAuthority("USER")));

        // Detail을 넣어줌
        authenticationToken.setDetails(tokenUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info("[+] Token in SecurityContextHolder");
        filterChain.doFilter(request, response);
    }
}
