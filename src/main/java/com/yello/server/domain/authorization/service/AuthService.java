package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.OAUTH_TOKEN_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.TOKEN_ALL_EXPIRED_AUTH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.TOKEN_NOT_EXPIRED_AUTH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;
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
import com.yello.server.domain.authorization.exception.NotExpiredTokenForbiddenException;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.repository.SchoolRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.ListFactory;
import com.yello.server.global.common.factory.PaginationFactory;
import com.yello.server.global.common.util.RestUtil;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Builder
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final FriendRepository friendRepository;
    private final CooldownRepository cooldownRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenValueOperations;

    // TODO softDelete 우아하게 처리하는 방법으로 바꾸기
    public void renewUserInformation(User currentUser) {
        currentUser.renew();

        friendRepository.findAllByUserIdNotFiltered(currentUser.getId())
            .forEach(Friend::renew);
        friendRepository.findAllByTargetIdNotFiltered(currentUser.getId())
            .forEach(Friend::renew);
        cooldownRepository.findByUserIdNotFiltered(currentUser.getId())
            .ifPresent(Cooldown::renew);
    }

    // TODO 응답을 주입 받을 수 있도록 설계
    // TODO 테스트 코드 작성
    public OAuthResponse oauthLogin(OAuthRequest oAuthRequest) {
        final ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(
            oAuthRequest.accessToken());

        if (response.getStatusCode()==BAD_REQUEST || response.getStatusCode()==UNAUTHORIZED) {
            throw new OAuthException(OAUTH_TOKEN_EXCEPTION);
        }

        final Optional<User> target = userRepository.findByUuid(String.valueOf(response.getBody().id()));
        if (target.isEmpty()) {
            throw new NotSignedInException(NOT_SIGNIN_USER_EXCEPTION);
        }

        final User currentUser = target.get();
        final ServiceTokenVO serviceTokenVO =
            this.registerToken(currentUser.getId(), currentUser.getUuid());

        this.renewUserInformation(currentUser);
        tokenValueOperations.setDeviceToken(currentUser.getUuid(), oAuthRequest.deviceToken());
        return OAuthResponse.of(serviceTokenVO);
    }

    public Boolean isYelloIdDuplicated(String yelloId) {
        if (Objects.isNull(yelloId)) {
            throw new AuthBadRequestException(YELLOID_REQUIRED_EXCEPTION);
        }

        return userRepository.findByYelloId(yelloId).isPresent();
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        final User signUpUser = this.signUpUser(signUpRequest);
        this.recommendUser(signUpRequest.recommendId());
        final ServiceTokenVO signUpToken = this.registerToken(signUpUser.getId(), signUpUser.getUuid());
        this.makeFriend(signUpUser, signUpRequest.friends());

        return SignUpResponse.of(signUpUser.getYelloId(), signUpToken);
    }

    public User signUpUser(SignUpRequest signUpRequest) {
        // exception
        userRepository.findByUuid(signUpRequest.uuid())
            .ifPresent(action -> {
                throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
            });

        userRepository.findByYelloId(signUpRequest.yelloId())
            .ifPresent(action -> {
                throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
            });

        School group = schoolRepository.getById(signUpRequest.groupId());

        final User newSignInUser = userRepository.save(User.of(signUpRequest, group));
        tokenValueOperations.setDeviceToken(newSignInUser.getUuid(), signUpRequest.deviceToken());
        return newSignInUser;
    }

    public void recommendUser(String recommendYelloId) {
        if (recommendYelloId!=null && !recommendYelloId.isEmpty()) {
            User recommendedUser = userRepository.getByYelloId(recommendYelloId);
            recommendedUser.increaseRecommendCount();

            final Optional<Cooldown> cooldown =
                cooldownRepository.findByUserId(recommendedUser.getId());
            cooldown.ifPresent(cooldownRepository::delete);
        }
    }

    public ServiceTokenVO registerToken(Long id, String uuid) {
        ServiceTokenVO newUserTokens = jwtTokenProvider.createServiceToken(
            id,
            uuid
        );
        tokenValueOperations.set(id, newUserTokens);

        return newUserTokens;
    }

    public void makeFriend(User user, List<Long> friendIds) {
        friendIds
            .stream()
            .map(userRepository::findById)
            .forEach(friend -> {
                if (friend.isPresent()) {
                    friendRepository.save(Friend.createFriend(user, friend.get()));
                    friendRepository.save(Friend.createFriend(friend.get(), user));
                }
            });
    }

    public OnBoardingFriendResponse findOnBoardingFriends(OnBoardingFriendRequest friendRequest,
        Pageable pageable) {

        final List<User> kakaoFriends = ListFactory.toNonNullableList(friendRequest.friendKakaoId()
            .stream()
            .map(String::valueOf)
            .map(userRepository::findByUuid)
            .toList());

        final List<User> totalList = kakaoFriends
            .stream()
            .distinct()
            .sorted(Comparator.comparing(User::getName))
            .toList();

        final List<User> pageList = PaginationFactory.getPage(totalList, pageable)
            .stream()
            .toList();

        return OnBoardingFriendResponse.of(totalList.size(), pageList);
    }

    public GroupNameSearchResponse findSchoolsBySearch(String keyword, Pageable pageable) {
        int totalCount = schoolRepository.countDistinctSchoolNameContaining(keyword);
        final List<String> nameList = schoolRepository.findDistinctSchoolNameContaining(keyword,
            pageable);
        return GroupNameSearchResponse.of(totalCount, nameList);
    }

    public DepartmentSearchResponse findDepartmentsBySearch(String schoolName, String keyword,
        Pageable pageable) {
        int totalCount = schoolRepository.countAllBySchoolNameContaining(schoolName, keyword);
        final List<School> schoolResult = schoolRepository.findAllBySchoolNameContaining(schoolName,
            keyword, pageable);
        return DepartmentSearchResponse.of(totalCount, schoolResult);
    }

    public ServiceTokenVO reIssueToken(@NotNull ServiceTokenVO tokens) {
        boolean isAccessTokenExpired = jwtTokenProvider.isExpired(tokens.accessToken());
        boolean isRefreshTokenExpired = jwtTokenProvider.isExpired(tokens.refreshToken());

        if (isAccessTokenExpired) {

            if (isRefreshTokenExpired) {
                throw new NotExpiredTokenForbiddenException(TOKEN_ALL_EXPIRED_AUTH_EXCEPTION);
            }

            val refreshToken = tokens.refreshToken();
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            String uuid = jwtTokenProvider.getUserUuid(refreshToken);

            userRepository.getById(userId);
            userRepository.getByUuid(uuid);

            String newAccessToken = jwtTokenProvider.createAccessToken(userId, uuid);
            val token = ServiceTokenVO.of(newAccessToken, tokens.refreshToken());
            tokenValueOperations.set(userId, token);
            return token;
        }

        throw new NotExpiredTokenForbiddenException(TOKEN_NOT_EXPIRED_AUTH_EXCEPTION);
    }
}
