package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.TOKEN_ALL_EXPIRED_AUTH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.TOKEN_NOT_EXPIRED_AUTH_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_REQUIRED_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;
import static com.yello.server.global.common.util.ConstantUtil.RECOMMEND_POINT;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.kakao.KakaoTokenInfo;
import com.yello.server.domain.authorization.dto.request.OAuthRequest;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.ClassNameSearchResponse;
import com.yello.server.domain.authorization.dto.response.DepartmentSearchResponse;
import com.yello.server.domain.authorization.dto.response.GroupNameSearchResponse;
import com.yello.server.domain.authorization.dto.response.OAuthResponse;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.exception.NotExpiredTokenForbiddenException;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.friend.service.FriendManager;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserData;
import com.yello.server.domain.user.entity.UserDataType;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.global.common.factory.PaginationFactory;
import com.yello.server.global.common.manager.ConnectionManager;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Builder
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthManager authManager;
    private final ConnectionManager connectionManager;
    private final CooldownRepository cooldownRepository;
    private final FriendManager friendManager;
    private final FriendRepository friendRepository;
    private final MessageQueueRepository messageQueueRepository;
    private final NotificationService notificationService;
    private final UserDataRepository userDataRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final VoteManager voteManager;

    @Transactional
    public OAuthResponse oauthLogin(OAuthRequest oAuthRequest) {
        final ResponseEntity<KakaoTokenInfo> response =
            connectionManager.getKakaoTokenInfo(oAuthRequest.accessToken());

        final User user = authManager.getSignedInUserByUuid(response.getBody().id().toString());
        final ServiceTokenVO serviceTokenVO = authManager.issueToken(user);

        final Boolean isResigned = authManager.renewUserData(user);
        user.setDeviceToken(oAuthRequest.deviceToken());
        return OAuthResponse.of(isResigned, serviceTokenVO);
    }

    public Boolean isYelloIdDuplicated(String yelloId) {
        if (Objects.isNull(yelloId)) {
            throw new AuthBadRequestException(YELLOID_REQUIRED_EXCEPTION);
        }

        return userRepository.findByYelloIdNotFiltered(yelloId).isPresent();
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        authManager.validateSignupRequest(signUpRequest);
        final UserGroup group = userGroupRepository.getById(signUpRequest.groupId());

        final User newUser = userRepository.save(User.of(signUpRequest, group));
        newUser.setDeviceToken(signUpRequest.deviceToken());

        this.recommendUser(signUpRequest.recommendId(), signUpRequest.yelloId());

        final ServiceTokenVO signUpToken = authManager.issueToken(newUser);

        this.makeFriend(newUser, signUpRequest.friends());

        voteManager.makeGreetingVote(newUser);
        return SignUpResponse.of(newUser.getYelloId(), signUpToken);
    }

    @Transactional
    public void recommendUser(String recommendYelloId, String userYelloId) {
        if (recommendYelloId!=null && !recommendYelloId.isEmpty()) {
            User recommendedUser = userRepository.getByYelloId(recommendYelloId);
            User user = userRepository.getByYelloId(userYelloId);
            final Optional<UserData> recommended =
                userDataRepository.findByUserIdAndTag(recommendedUser.getId(),
                    UserDataType.RECOMMENDED);

            recommendedUser.addRecommendCount(1L);
            recommendedUser.addPointBySubscribe(RECOMMEND_POINT);
            user.addPointBySubscribe(RECOMMEND_POINT);

            final Optional<Cooldown> cooldown =
                cooldownRepository.findByUserId(recommendedUser.getId());
            cooldown.ifPresent(cooldownRepository::delete);

            if (recommended.isEmpty()) {
                recommendedUser.addTicketCount(1);
                notificationService.sendRecommendSignupAndGetTicketNotification(recommendedUser,
                    user);

                userDataRepository.save(UserData.of(
                    UserDataType.RECOMMENDED,
                    ZonedDateTime.now(GlobalZoneId).format(ISO_OFFSET_DATE_TIME),
                    recommendedUser
                ));
                return;
            }
            notificationService.sendRecommendNotification(user, recommendedUser);
        }
    }

    @Transactional
    public void makeFriend(User user, List<Long> friendIds) {
        friendIds
            .stream()
            .map(userRepository::findById)
            .forEach(friend -> {
                if (friend.isPresent()) {
                    Friend savedFriend =
                        friendRepository.save(Friend.createFriend(user, friend.get()));
                    friendRepository.save(Friend.createFriend(friend.get(), user));

                    notificationService.sendFriendNotification(savedFriend);
                }
            });
    }

    public OnBoardingFriendResponse findOnBoardingFriends(OnBoardingFriendRequest friendRequest,
        Pageable pageable) {
        final List<User> kakaoFriends =
            friendManager.getRecommendedFriendsForOnBoarding(friendRequest.friendKakaoId());

        final List<User> pageList = PaginationFactory.getPage(kakaoFriends, pageable)
            .stream()
            .toList();

        return OnBoardingFriendResponse.of(kakaoFriends.size(), pageList);
    }

    public GroupNameSearchResponse findGroupNameContaining(String keyword,
        UserGroupType userGroupType,
        Pageable pageable) {
        int totalCount =
            userGroupRepository.countDistinctGroupNameContaining(keyword, userGroupType);
        final List<String> nameList =
            userGroupRepository.findDistinctGroupNameContaining(keyword, userGroupType,
                    pageable)
                .stream()
                .toList();

        return GroupNameSearchResponse.of(totalCount, nameList);
    }

    public DepartmentSearchResponse findGroupDepartmentBySchoolNameContaining(String schoolName,
        String keyword,
        UserGroupType userGroupType, Pageable pageable) {
        int totalCount =
            userGroupRepository.countAllByGroupNameContaining(schoolName, keyword, userGroupType);
        final List<UserGroup> userGroupResult =
            userGroupRepository.findAllByGroupNameContaining(schoolName, keyword,
                userGroupType, pageable);

        return DepartmentSearchResponse.of(totalCount, userGroupResult);
    }

    @Transactional
    public ServiceTokenVO reIssueToken(@NotNull ServiceTokenVO tokens) {
        boolean isAccessTokenExpired = authManager.isExpired(tokens.accessToken());
        boolean isRefreshTokenExpired = authManager.isExpired(tokens.refreshToken());

        if (isAccessTokenExpired) {

            if (isRefreshTokenExpired) {
                throw new NotExpiredTokenForbiddenException(TOKEN_ALL_EXPIRED_AUTH_EXCEPTION);
            }

            String newAccessToken = authManager.issueNewAccessToken(tokens.refreshToken());
            return ServiceTokenVO.of(newAccessToken, tokens.refreshToken());
        }

        throw new NotExpiredTokenForbiddenException(TOKEN_NOT_EXPIRED_AUTH_EXCEPTION);
    }

    public ClassNameSearchResponse getHighSchoolClassName(String schoolName, String className) {
        UserGroup userGroup =
            userGroupRepository.getByGroupNameAndSubGroupName(schoolName, className,
                UserGroupType.HIGH_SCHOOL);
        return ClassNameSearchResponse.of(userGroup);
    }
}
