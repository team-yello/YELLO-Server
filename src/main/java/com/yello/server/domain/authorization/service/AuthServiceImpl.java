package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.OAUTH_TOKEN_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.dto.KakaoTokenInfo;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.common.util.RestUtil;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;

    @Override
    public OAuthResponse oauthLogin(OAuthRequest authRequestDto, HttpServletRequest request) {
        ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(authRequestDto.accessToken());

        if (response.getStatusCode()==BAD_REQUEST || response.getStatusCode()==UNAUTHORIZED) {
            throw new OAuthException(OAUTH_TOKEN_EXCEPTION);
        }

        Optional<User> user = userRepository.findByUuid(String.valueOf(response.getBody().id()));

        if (user.isEmpty()) {
            throw new NotSignedInException(NOT_SIGNIN_USER_EXCEPTION);
        }

        User currentUser = user.get();
        ServiceTokenVO serviceTokenVO = jwtTokenProvider.createServiceToken(currentUser.getId(), currentUser.getUuid());
        tokenValueOperations.set(
            currentUser.getId(),
            serviceTokenVO
        );

        return OAuthResponse.of(serviceTokenVO);
    }
}
