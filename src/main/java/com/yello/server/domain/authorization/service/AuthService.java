package com.yello.server.domain.authorization.service;

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
import com.yello.server.domain.authorization.exception.OAuthException;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.repository.SchoolRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.global.common.factory.PaginationFactory;
import com.yello.server.global.common.util.RestUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Builder
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {


  private final UserRepository userRepository;
  private final SchoolRepository schoolRepository;
  private final FriendRepository friendRepository;
  private final CooldownRepository cooldownRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final ValueOperations<Long, ServiceTokenVO> tokenValueOperations;

  @Transactional
  public OAuthResponse oauthLogin(OAuthRequest oAuthRequest) {
    final ResponseEntity<KakaoTokenInfo> response = RestUtil.getKakaoTokenInfo(
        oAuthRequest.accessToken());

    if (response.getStatusCode() == BAD_REQUEST || response.getStatusCode() == UNAUTHORIZED) {
      throw new OAuthException(OAUTH_TOKEN_EXCEPTION);
    }

    final User currentUser = userRepository.findByUuid(String.valueOf(response.getBody().id()));

    final ServiceTokenVO serviceTokenVO = jwtTokenProvider.createServiceToken(
        currentUser.getId(),
        currentUser.getUuid()
    );

    tokenValueOperations.set(
        currentUser.getId(),
        serviceTokenVO
    );

    currentUser.renew();
    friendRepository.findAllByUserIdNotFiltered(currentUser.getId())
        .forEach(Friend::renew);
    friendRepository.findAllByTargetIdNotFiltered(currentUser.getId())
        .forEach(Friend::renew);
    cooldownRepository.findByUserIdNotFiltered(currentUser.getId())
        .ifPresent(Cooldown::renew);

    return OAuthResponse.of(serviceTokenVO);
  }

  public Boolean isYelloIdDuplicated(String yelloId) {
    if (Objects.isNull(yelloId)) {
      throw new AuthBadRequestException(YELLOID_REQUIRED_EXCEPTION);
    }

    try {
      userRepository.findByYelloId(yelloId);
    } catch (UserNotFoundException e) {
      return false;
    }

    return true;
  }

  @Transactional
  public SignUpResponse signUp(SignUpRequest signUpRequest) {
    // exception
    final User userByUUID = userRepository.findByUuid(signUpRequest.uuid());
    if (ObjectUtils.isNotEmpty(userByUUID)) {
      throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
    }

    final User userByYelloId = userRepository.findByYelloId(signUpRequest.yelloId());
    if (ObjectUtils.isNotEmpty(userByYelloId)) {
      throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
    }

    School group = schoolRepository.findById(signUpRequest.groupId());

    final User newSignInUser = userRepository.save(
        User.of(signUpRequest, signUpRequest.uuid(), group));
    ServiceTokenVO newUserTokens = jwtTokenProvider.createServiceToken(
        newSignInUser.getId(),
        newSignInUser.getUuid()
    );

    if (signUpRequest.recommendId() != null && !"".equals(signUpRequest.recommendId())) {
      User recommendedUser = userRepository.findByYelloId(signUpRequest.recommendId());
      recommendedUser.increaseRecommendCount();

      final Optional<Cooldown> cooldown = cooldownRepository.findByUserId(recommendedUser.getId());
      cooldown.ifPresent(cooldownRepository::delete);
    }

    signUpRequest.friends()
        .stream()
        .map(userRepository::findById)
        .forEach(friend -> {
          friendRepository.save(Friend.createFriend(newSignInUser, friend));
          friendRepository.save(Friend.createFriend(friend, newSignInUser));
        });

    tokenValueOperations.set(newSignInUser.getId(), newUserTokens);
    return SignUpResponse.of(newSignInUser.getYelloId(), newUserTokens);
  }

  public OnBoardingFriendResponse findOnBoardingFriends(OnBoardingFriendRequest friendRequest,
      Pageable pageable) {
    List<User> totalList = new ArrayList<>();

    schoolRepository.findById(friendRequest.groupId());

    final List<User> groupFriends = userRepository.findAllByGroupId(friendRequest.groupId());
    final List<User> kakaoFriends = friendRequest.friendKakaoId()
        .stream()
        .map(String::valueOf)
        .map(userRepository::findByUuid)
        .toList();

    totalList.addAll(groupFriends);
    totalList.addAll(kakaoFriends);

    totalList = totalList.stream()
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

      userRepository.findById(userId);
      userRepository.findByUuid(uuid);

      String newAccessToken = jwtTokenProvider.createAccessToken(userId, uuid);
      val token = ServiceTokenVO.of(newAccessToken, tokens.refreshToken());
      tokenValueOperations.set(userId, token);
      return token;
    }

    throw new NotExpiredTokenForbiddenException(TOKEN_NOT_EXPIRED_AUTH_EXCEPTION);
  }
}
