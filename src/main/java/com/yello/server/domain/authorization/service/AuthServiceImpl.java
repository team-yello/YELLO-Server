package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.*;
import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.OAUTH_TOKEN_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.dto.KakaoTokenInfo;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.SignInRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.SignInResponse;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.entity.SchoolRepository;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserBadRequestException;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.global.common.util.RestUtil;

import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final FriendRepository friendRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;

    @Override
    public OAuthResponse oauthLogin(OAuthRequest oAuthRequest) {
        ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(oAuthRequest.accessToken());

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

    @Override
    public Boolean isYelloIdDuplicated(String yelloId) {
        if(Objects.isNull(yelloId)){
            throw new AuthBadRequestException(YELLOID_REQUIRED_EXCEPTION);
        }

        User user = userRepository.findByYelloId(yelloId)
                .orElseThrow(() -> { throw new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION); });

        return true;
    }

    @Override
    @Transactional(readOnly = false)
    public SignInResponse signIn(String oAuthAccessToken, SignInRequest signInRequest) {
        String socialUUID = "";

        // exception
        if(Objects.isNull(oAuthAccessToken)) {
            throw new AuthBadRequestException(OAUTH_ACCESS_TOKEN_REQUIRED_EXCEPTION);
        }

        if(Social.KAKAO == signInRequest.social()) {
            ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(oAuthAccessToken);

            if (response.getStatusCode()==BAD_REQUEST || response.getStatusCode()==UNAUTHORIZED) {
                throw new OAuthException(OAUTH_TOKEN_EXCEPTION);
            }

            socialUUID = String.valueOf(response.getBody().id());
        }

        Optional<User> userByUUID = userRepository.findByUuid(socialUUID);
        if(userByUUID.isPresent()){
            throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
        }

        Optional<User> userByYelloId = userRepository.findByYelloId(signInRequest.yelloId());
        if(userByYelloId.isPresent()){
            throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
        }

        School group = schoolRepository.findById(signInRequest.groupId())
                .orElseThrow(() -> { throw new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION); });

        // logic
        User newSignInUser = userRepository.save(User.of(signInRequest, socialUUID, group));
        ServiceTokenVO newUserTokens = jwtTokenProvider.createServiceToken(newSignInUser.getId(), newSignInUser.getUuid());

        if(!Objects.isNull(signInRequest.recommendId())){
            User recommendedUser = userRepository.findByYelloId(signInRequest.recommendId())
                    .orElseThrow(() -> { throw new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION); });

            recommendedUser.addRecommendCount(1);
        }

        for(Long friendId : signInRequest.friends()){
            User friend = userRepository.findById(friendId)
                    .orElseThrow(() -> { throw new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION); });

            friendRepository.save(Friend.createFriend(newSignInUser, friend));
        }

        // redis
        tokenValueOperations.set(newSignInUser.getId(), newUserTokens);

        return SignInResponse.of(newSignInUser.getUuid(), newUserTokens);
    }
}
