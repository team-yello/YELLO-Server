package com.yello.server.domain.user.service;

import com.yello.server.domain.user.dto.request.OAuthRequest;
import com.yello.server.domain.user.dto.response.KakaoTokenInfo;
import com.yello.server.domain.user.dto.response.OAuthResponse;
import com.yello.server.domain.user.dto.response.ServiceTokenVO;
import com.yello.server.domain.user.dto.response.UserInfoResponse;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.util.AuthTokenProvider;
import com.yello.server.global.common.ErrorCode;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private String KAKAO_TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";

    private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;
    private final UserRepository userRepository;
    private final AuthTokenProvider authTokenProvider;

    @Transactional
    @Override
    public OAuthResponse oauthLogin(OAuthRequest authRequestDto, HttpServletRequest request) {
        Map<String, String> tokens = null;
        User newUser = null;

        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_TOKEN_INFO_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authRequestDto.accessToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        ResponseEntity<KakaoTokenInfo> response = webClient.get()
                .exchangeToMono(clientResponse -> clientResponse.toEntity(KakaoTokenInfo.class))
                .block();

//        System.out.println("response = " + response);

        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#get-token-info
        if(response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode() == HttpStatus.UNAUTHORIZED)
            throw new CustomException(ErrorCode.KAKAO_TOKEN_EXCEPTION, ErrorCode.KAKAO_TOKEN_EXCEPTION.getMessage());

        Optional<User> user = userRepository.findByUuid(String.valueOf(response.getBody().id()));

        if(user.isEmpty()) {
            newUser = userRepository.save(
                    User.builder()
                            .name("")
                            .recommendCount(0L)
                            .yelloId("")
                            .gender(Gender.FEMALE)
                            .social(Social.KAKAO)
                            .uuid(String.valueOf(response.getBody().id()))
                            .deletedAt(LocalDateTime.now())
                            .point(0)
                            .build());
            tokens = authTokenProvider.createToken(newUser.getId(), newUser.getUuid());
        } else {
            tokens = authTokenProvider.createToken(user.get().getId(), user.get().getUuid());
        }

        String accessToken = tokens.get(AuthTokenProvider.ACCESS_TOKEN);
        String refreshToken = tokens.get(AuthTokenProvider.REFRESH_TOKEN);

        tokenValueOperations.set(user.isEmpty() ? newUser.getId() : user.get().getId(),
                ServiceTokenVO.builder().accessToken(accessToken).refreshToken(refreshToken).build());

        return OAuthResponse.of(user.isPresent(), accessToken, refreshToken);
    }

    @Override
    public UserInfoResponse findUserInfo(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(!authTokenProvider.validateToken(accessToken))
            throw new CustomException(ErrorCode.TOKEN_EXPIRED_EXCEPTION, ErrorCode.TOKEN_EXPIRED_EXCEPTION.getMessage());

        Long userId = authTokenProvider.parseAccessToken(accessToken).id();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_EXCEPTION, ErrorCode.NOT_FOUND_USER_EXCEPTION.getMessage()));

        return UserInfoResponse.of(user);
    }
}
