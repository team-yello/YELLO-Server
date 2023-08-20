package com.yello.server.domain.authorization.service;

import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Component
@RequiredArgsConstructor
public class AuthManagerImpl implements AuthManager {

    private final FriendRepository friendRepository;
    private final CooldownRepository cooldownRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    public User getSignedInUserByUuid(String uuid) {
        final Optional<User> target = userRepository.findByUuid(String.valueOf(uuid));
        if (target.isEmpty()) {
            throw new NotSignedInException(NOT_SIGNIN_USER_EXCEPTION);
        }
        return target.get();
    }

    @Override
    public ServiceTokenVO registerToken(User user) {
        ServiceTokenVO newUserTokens = tokenProvider.createServiceToken(
            user.getId(),
            user.getUuid()
        );
        tokenRepository.set(user.getId(), newUserTokens);
        return newUserTokens;
    }

    @Override
    public ServiceTokenVO setNewAccessToken(String refreshToken) {
        final Long userId = tokenProvider.getUserId(refreshToken);
        final String uuid = tokenProvider.getUserUuid(refreshToken);

        userRepository.getById(userId);
        userRepository.getByUuid(uuid);

        final String newAccessToken = tokenProvider.createAccessToken(userId, uuid);
        final ServiceTokenVO token = ServiceTokenVO.of(newAccessToken, refreshToken);
        tokenRepository.set(userId, token);
        return token;
    }

    @Override
    public void validateSignupRequest(SignUpRequest signUpRequest) {
        userRepository.findByUuid(signUpRequest.uuid())
            .ifPresent(action -> {
                throw new UserConflictException(UUID_CONFLICT_USER_EXCEPTION);
            });

        userRepository.findByYelloId(signUpRequest.yelloId())
            .ifPresent(action -> {
                throw new UserConflictException(YELLOID_CONFLICT_USER_EXCEPTION);
            });
    }

    @Override
    public Boolean renewUserData(User user) {
        final Long userId = user.getId();

        if (user.getDeletedAt()!=null) {
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
