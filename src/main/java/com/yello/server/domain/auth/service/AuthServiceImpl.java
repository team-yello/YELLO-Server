package com.yello.server.domain.auth.service;

import com.yello.server.domain.auth.dto.request.OAuthRequest;
import com.yello.server.domain.auth.util.AuthTokenProvider;
import com.yello.server.global.common.dto.kakao.KakaoTokenInfo;
import com.yello.server.domain.auth.dto.response.OAuthResponse;
import com.yello.server.domain.auth.dto.ServiceTokenVO;
import com.yello.server.domain.auth.dto.response.UserInfoResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.global.common.util.RestUtil;
import com.yello.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;
    private final UserRepository userRepository;
    private final AuthTokenProvider authTokenProvider;

    @Override
    @Transactional(readOnly = false)
    public OAuthResponse oauthLogin(OAuthRequest authRequestDto, HttpServletRequest request) {
        ServiceTokenVO tokens;

        ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(authRequestDto.accessToken());

        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#get-token-info
        if(response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new CustomException(ErrorCode.KAKAO_TOKEN_EXCEPTION, ErrorCode.KAKAO_TOKEN_EXCEPTION.getMessage());
        }

        Optional<User> user = userRepository.findByUuid(String.valueOf(response.getBody().id()));

        if(user.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_SIGNIN_USER_EXCEPTION, ErrorCode.NOT_SIGNIN_USER_EXCEPTION.getMessage());
        }

        tokens = authTokenProvider.createToken(user.get().getId(), user.get().getUuid());
        tokenValueOperations.set(user.get().getId(), ServiceTokenVO.of(tokens.accessToken(), tokens.refreshToken()));

        return OAuthResponse.of(tokens);
    }

    @Override
    @Transactional(readOnly = false)
    public UserInfoResponse findUserInfo(String accessToken) {
        if(!authTokenProvider.validateToken(accessToken))
            throw new CustomException(ErrorCode.TOKEN_EXPIRED_EXCEPTION, ErrorCode.TOKEN_EXPIRED_EXCEPTION.getMessage());

        Long userId = authTokenProvider.parseAccessToken(accessToken).id();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        return UserInfoResponse.of(user);
    }
}
