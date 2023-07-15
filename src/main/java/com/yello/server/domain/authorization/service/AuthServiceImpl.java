package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.*;
import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.OAUTH_TOKEN_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
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
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.global.common.util.ListUtil;
import com.yello.server.global.common.util.PaginationUtil;
import com.yello.server.global.common.util.RestUtil;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));

        return true;
    }

    @Override
    @Transactional
    public SignUpResponse signUp(String oAuthAccessToken, SignUpRequest signUpRequest) {
        String socialUUID = "";

        // exception
        if(Objects.isNull(oAuthAccessToken)) {
            throw new AuthBadRequestException(OAUTH_ACCESS_TOKEN_REQUIRED_EXCEPTION);
        }

        if(Social.KAKAO == signUpRequest.social()) {
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

        Optional<User> userByYelloId = userRepository.findByYelloId(signUpRequest.yelloId());
        if(userByYelloId.isPresent()){
            throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
        }

        School group = schoolRepository.findById(signUpRequest.groupId())
                .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));

        // logic
        User newSignInUser = userRepository.save(User.of(signUpRequest, socialUUID, group));
        ServiceTokenVO newUserTokens = jwtTokenProvider.createServiceToken(newSignInUser.getId(), newSignInUser.getUuid());

        if(signUpRequest.recommendId() != null){
            User recommendedUser = userRepository.findByYelloId(signUpRequest.recommendId())
                    .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));

            recommendedUser.addRecommendCount(1);
        }

        signUpRequest.friends()
                .stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .forEach(friend -> friendRepository.save(Friend.createFriend(newSignInUser, friend.get())));

        // redis
        tokenValueOperations.set(newSignInUser.getId(), newUserTokens);

        return SignUpResponse.of(newSignInUser.getYelloId(), newUserTokens);
    }

    @Override
    public List<OnBoardingFriendResponse> findOnBoardingFriends(OnBoardingFriendRequest friendRequest, Pageable pageable, User loginUser) {
        List<User> totalList = new ArrayList<>();

        // exception
        schoolRepository.findById(friendRequest.groupId())
                .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));

        // logic
        val groupFriends = userRepository.findAllByGroupId(friendRequest.groupId());
        val kakaoFriends = friendRequest.friendKakaoId()
                .stream()
                .map(String::valueOf)
                .map(userRepository::findByUuid)
                .toList();

        totalList.addAll(groupFriends);
        totalList.addAll(ListUtil.toList(kakaoFriends));

        totalList = totalList.stream().distinct().toList();
        totalList = totalList.stream().filter(user -> !Objects.equals(user.getId(), loginUser.getId())).toList();
        totalList = totalList.stream().sorted(Comparator.comparing(User::getName)).toList();

        val responseList = OnBoardingFriendResponse.listOf(totalList);

        return PaginationUtil.getPage(responseList, pageable).stream().toList();
    }
}
