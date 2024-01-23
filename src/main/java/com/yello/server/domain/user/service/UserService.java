package com.yello.server.domain.user.service;

import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_CONFLICT_USER_EXCEPTION;

import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.dto.request.UserDeviceTokenRequest;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserDetailV2Response;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.dto.response.UserSubscribeDetailResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final CooldownRepository cooldownRepository;
    private final FriendRepository friendRepository;
    private final PurchaseRepository purchaseRepository;
    private final TokenRepository tokenRepository;
    private final UserAdminRepository userAdminRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public UserDetailResponse findMyProfile(Long userId) {
        final User user = userRepository.getById(userId);
        final Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        final Integer friendCount = friendRepository.findAllByUserId(user.getId()).size();

        return UserDetailResponse.of(user, yelloCount, friendCount);
    }

    public UserDetailV2Response getUserDetailV2(Long userId) {
        final User user = userRepository.getById(userId);
        final Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        final Integer friendCount = friendRepository.findAllByUserId(user.getId()).size();

        return UserDetailV2Response.of(user, user.getGroup(), yelloCount, friendCount);
    }

    public UserResponse findUserById(Long userId) {
        final User user = userRepository.getById(userId);
        final Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        final Integer friendCount = friendRepository.findAllByUserId(user.getId()).size();

        return UserResponse.of(user, yelloCount, friendCount);
    }

    @Transactional
    public EmptyObject updateUserDeviceToken(User user, UserDeviceTokenRequest request) {
        final User updateUser = userRepository.getById(user.getId());
        userRepository.findByDeviceToken(request.deviceToken())
            .ifPresent(action -> {
                throw new UserConflictException(DEVICE_TOKEN_CONFLICT_USER_EXCEPTION);
            });
        updateUser.setDeviceToken(request.deviceToken());

        return EmptyObject.builder().build();
    }

    @Transactional
    public void delete(User user) {
        final User target = userRepository.getById(user.getId());
        target.delete();

        friendRepository.findAllByUserId(target.getId())
            .forEach(Friend::delete);

        friendRepository.findAllByTargetId(target.getId())
            .forEach(Friend::delete);

        cooldownRepository.findByUserId(target.getId())
            .ifPresent(Cooldown::delete);
    }

    public UserSubscribeDetailResponse getUserSubscribe(Long userId) {
        final User user = userRepository.getById(userId);
        final Purchase purchase = purchaseRepository.getTopByStateAndUserId(user);

        return UserSubscribeDetailResponse.of(purchase);
    }
}
