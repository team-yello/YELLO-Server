package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.AUTH_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.AUTH_UUID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.OAUTH_TOKEN_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.TOKEN_ALL_EXPIRED_AUTH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.TOKEN_NOT_EXPIRED_AUTH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_REQUIRED_EXCEPTION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.yello.server.domain.authorization.JwtTokenProvider;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.DepartmentSearchResponse;
import com.yello.server.domain.authorization.dto.response.GroupNameSearchResponse;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.exception.AuthNotFoundException;
import com.yello.server.domain.authorization.exception.NotExpiredTokenForbiddenException;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.entity.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.entity.SchoolRepository;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.global.common.util.ListUtil;
import com.yello.server.global.common.util.PaginationUtil;
import com.yello.server.global.common.util.RestUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final FriendRepository friendRepository;
    private final CooldownRepository cooldownRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;

    @Transactional
    @Override
    public OAuthResponse oauthLogin(OAuthRequest oAuthRequest) {
        ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(oAuthRequest.accessToken());

        if (response.getStatusCode()==BAD_REQUEST || response.getStatusCode()==UNAUTHORIZED) {
            throw new OAuthException(OAUTH_TOKEN_EXCEPTION);
        }

        User currentUser = userRepository.findByUuid(String.valueOf(response.getBody().id()))
            .orElseThrow(() -> new NotSignedInException(NOT_SIGNIN_USER_EXCEPTION));

        ServiceTokenVO serviceTokenVO = jwtTokenProvider.createServiceToken(currentUser.getId(), currentUser.getUuid());
        tokenValueOperations.set(
            currentUser.getId(),
            serviceTokenVO
        );

        currentUser.renew();
        friendRepository.findAllByUserIdNotFiltered(currentUser.getId())
            .forEach(Friend::renew);
        cooldownRepository.findByUserIdNotFiltered(currentUser.getId())
            .ifPresent(Cooldown::renew);

        return OAuthResponse.of(serviceTokenVO);
    }

    @Override
    public Boolean isYelloIdDuplicated(String yelloId) {
        if (Objects.isNull(yelloId)) {
            throw new AuthBadRequestException(YELLOID_REQUIRED_EXCEPTION);
        }

        User user = userRepository.findByYelloId(yelloId)
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));

        return true;
    }

    @Transactional
    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        // exception
        Optional<User> userByUUID = userRepository.findByUuid(signUpRequest.uuid());
        if (userByUUID.isPresent()) {
            throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
        }

        Optional<User> userByYelloId = userRepository.findByYelloId(signUpRequest.yelloId());
        if (userByYelloId.isPresent()) {
            throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
        }

        School group = schoolRepository.findById(signUpRequest.groupId())
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));

        User newSignInUser = userRepository.save(User.of(signUpRequest, signUpRequest.uuid(), group));
        ServiceTokenVO newUserTokens = jwtTokenProvider.createServiceToken(
            newSignInUser.getId(),
            newSignInUser.getUuid()
        );

        if (signUpRequest.recommendId()!=null && !"".equals(signUpRequest.recommendId())) {
            User recommendedUser = userRepository.findByYelloId(signUpRequest.recommendId())
                .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));

            recommendedUser.addRecommendCount(1);

            Optional<Cooldown> cooldown = cooldownRepository.findByUser(recommendedUser);
            cooldown.ifPresent(cooldownRepository::delete);
        }

        signUpRequest.friends()
            .stream()
            .map(userRepository::findById)
            .filter(Optional::isPresent)
            .forEach(friend -> {
                friendRepository.save(Friend.createFriend(newSignInUser, friend.get()));
                friendRepository.save(Friend.createFriend(friend.get(), newSignInUser));
            });

        tokenValueOperations.set(newSignInUser.getId(), newUserTokens);
        return SignUpResponse.of(newSignInUser.getYelloId(), newUserTokens);
    }

    @Override
    public OnBoardingFriendResponse findOnBoardingFriends(OnBoardingFriendRequest friendRequest, Pageable pageable) {
        List<User> totalList = new ArrayList<>();

        schoolRepository.findById(friendRequest.groupId())
            .orElseThrow(() -> new GroupNotFoundException(GROUPID_NOT_FOUND_GROUP_EXCEPTION));

        val groupFriends = userRepository.findAllByGroupId(friendRequest.groupId());
        val kakaoFriends = friendRequest.friendKakaoId()
            .stream()
            .map(String::valueOf)
            .map(userRepository::findByUuid)
            .toList();

        totalList.addAll(groupFriends);
        totalList.addAll(ListUtil.toList(kakaoFriends));

        totalList = totalList.stream()
            .distinct()
            .sorted(Comparator.comparing(User::getName))
            .toList();

        val pageList = PaginationUtil.getPage(totalList, pageable)
            .stream()
            .toList();

        return OnBoardingFriendResponse.of(totalList.size(), pageList);
    }

    @Override
    public GroupNameSearchResponse findSchoolsBySearch(String keyword, Pageable pageable) {
        int totalCount = schoolRepository.countDistinctSchoolNameContaining(keyword);
        List<String> nameList = schoolRepository.findDistinctSchoolNameContaining(keyword, pageable);
        return GroupNameSearchResponse.of(totalCount, nameList);
    }

    @Override
    public DepartmentSearchResponse findDepartmentsBySearch(String schoolName, String keyword, Pageable pageable) {
        int totalCount = schoolRepository.countAllBySchoolNameContaining(schoolName, keyword);
        List<School> schoolResult = schoolRepository.findAllBySchoolNameContaining(schoolName, keyword, pageable);
        return DepartmentSearchResponse.of(totalCount, schoolResult);
    }

    @Override
    public ServiceTokenVO reIssueToken(@NotNull ServiceTokenVO tokens) {
        ServiceTokenVO newTokens = null;

        boolean isAccessTokenExpired = jwtTokenProvider.isExpired(tokens.accessToken());
        boolean isRefreshTokenExpired = jwtTokenProvider.isExpired(tokens.refreshToken());

        if (!isAccessTokenExpired && !isRefreshTokenExpired) {
            throw new NotExpiredTokenForbiddenException(TOKEN_NOT_EXPIRED_AUTH_EXCEPTION);
        } else if (isAccessTokenExpired && isRefreshTokenExpired) {
            throw new NotExpiredTokenForbiddenException(TOKEN_ALL_EXPIRED_AUTH_EXCEPTION);
        }

        if (isAccessTokenExpired && !isRefreshTokenExpired) {
            Long refreshTokenUserId = jwtTokenProvider.getUserId(tokens.refreshToken());
            String refreshUuid = jwtTokenProvider.getUserUuid(tokens.refreshToken());

            userRepository.findById(refreshTokenUserId)
                .orElseThrow(() -> new AuthNotFoundException(AUTH_NOT_FOUND_USER_EXCEPTION));
            userRepository.findByUuid(refreshUuid)
                .orElseThrow(() -> new AuthNotFoundException(AUTH_UUID_NOT_FOUND_USER_EXCEPTION));

            String newAccessToken = jwtTokenProvider.createAccessToken(refreshTokenUserId, refreshUuid);
            newTokens = ServiceTokenVO.of(newAccessToken, tokens.refreshToken());
        } else if (!isAccessTokenExpired && isRefreshTokenExpired) {
            Long accessTokenUserId = jwtTokenProvider.getUserId(tokens.accessToken());
            String accessUuid = jwtTokenProvider.getUserUuid(tokens.accessToken());

            userRepository.findById(accessTokenUserId)
                .orElseThrow(() -> new AuthNotFoundException(AUTH_NOT_FOUND_USER_EXCEPTION));
            userRepository.findByUuid(accessUuid)
                .orElseThrow(() -> new AuthNotFoundException(AUTH_UUID_NOT_FOUND_USER_EXCEPTION));

            String newRefreshToken = jwtTokenProvider.createRefreshToken(accessTokenUserId, accessUuid);
            newTokens = ServiceTokenVO.of(tokens.accessToken(), newRefreshToken);
        }

        tokenValueOperations.set(jwtTokenProvider.getUserId(newTokens.accessToken()), newTokens);
        return newTokens;
    }
}
