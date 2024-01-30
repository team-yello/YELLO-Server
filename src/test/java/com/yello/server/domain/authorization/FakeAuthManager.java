package com.yello.server.domain.authorization;

import static com.yello.server.global.common.ErrorCode.ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;

import com.yello.server.domain.admin.entity.AdminConfiguration;
import com.yello.server.domain.admin.entity.AdminConfigurationType;
import com.yello.server.domain.admin.exception.AdminConfigurationNotFoundException;
import com.yello.server.domain.admin.repository.AdminConfigurationRepository;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.service.AuthManager;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class FakeAuthManager implements AuthManager {

    private final AdminConfigurationRepository adminConfigurationRepository;
    private final CooldownRepository cooldownRepository;
    private final FriendRepository friendRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;


    public FakeAuthManager(AdminConfigurationRepository adminConfigurationRepository,
        CooldownRepository cooldownRepository,
        FriendRepository friendRepository, TokenProvider tokenProvider, UserRepository userRepository) {
        this.adminConfigurationRepository = adminConfigurationRepository;
        this.cooldownRepository = cooldownRepository;
        this.friendRepository = friendRepository;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public User getSignedInUserByUuid(String uuid) {
        final Optional<User> target = userRepository.findByUuid(String.valueOf(uuid));
        if (target.isEmpty()) {
            throw new NotSignedInException(NOT_SIGNIN_USER_EXCEPTION);
        }
        return target.get();
    }

    @Override
    public ServiceTokenVO issueToken(User user) {
        final List<AdminConfiguration> accessTokenTime = adminConfigurationRepository.findConfigurations(
            AdminConfigurationType.ACCESS_TOKEN_TIME);
        final List<AdminConfiguration> refreshTokenTime = adminConfigurationRepository.findConfigurations(
            AdminConfigurationType.REFRESH_TOKEN_TIME);

        if (accessTokenTime.isEmpty() || refreshTokenTime.isEmpty()) {
            throw new AdminConfigurationNotFoundException(ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION);
        }

        final String accessToken = tokenProvider.createAccessToken(
            user.getId(),
            user.getUuid(),
            Duration.ofMinutes(Long.parseLong(accessTokenTime.get(0).getValue()))
        );
        final String refreshToken = tokenProvider.createAccessToken(
            user.getId(),
            user.getUuid(),
            Duration.ofMinutes(Long.parseLong(refreshTokenTime.get(0).getValue()))
        );

        return ServiceTokenVO.of(accessToken, refreshToken);
    }

    @Override
    public boolean isExpired(String token) {
        return tokenProvider.isExpired(token);
    }

    @Override
    public String issueNewAccessToken(String refreshToken) {
        final Long userId = tokenProvider.getUserId(refreshToken);
        final String uuid = tokenProvider.getUserUuid(refreshToken);
        final List<AdminConfiguration> accessTokenTime = adminConfigurationRepository.findConfigurations(
            AdminConfigurationType.ACCESS_TOKEN_TIME);

        userRepository.getById(userId);
        userRepository.getByUuid(uuid);
        if (accessTokenTime.isEmpty()) {
            throw new AdminConfigurationNotFoundException(ADMIN_CONFIGURATION_NOT_FOUND_EXCEPTION);
        }

        final String newAccessToken = tokenProvider.createAccessToken(
            userId,
            uuid,
            Duration.ofMinutes(Long.parseLong(accessTokenTime.get(0).getValue()))
        );

        return newAccessToken;
    }

    @Override
    public void validateSignupRequest(SignUpRequest signUpRequest) {
        userRepository.findByUuid(signUpRequest.uuid())
            .ifPresent(action -> {
                throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
            });

        userRepository.findByYelloIdNotFiltered(signUpRequest.yelloId())
            .ifPresent(action -> {
                throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
            });
    }

    @Override
    public Boolean renewUserData(User user) {
        final Long userId = user.getId();

        if (user.getDeletedAt() != null) {
            user.renew();

            friendRepository.findAllByUserIdNotFiltered(userId)
                .forEach(Friend::renew);
            friendRepository.findAllByTargetIdNotFiltered(userId)
                .forEach(Friend::renew);
            cooldownRepository.findByUserIdNotFiltered(userId)
                .ifPresent(Cooldown::renew);

            return true;
        }
        return false;
    }
}
