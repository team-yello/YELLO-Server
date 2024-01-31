package com.yello.server.domain.user.service;

import static com.yello.server.domain.user.entity.UserDataType.ACCOUNT_UPDATED_AT;
import static com.yello.server.domain.user.entity.UserDataType.WITHDRAW_REASON;
import static com.yello.server.global.common.ErrorCode.DEVICE_TOKEN_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_DATA_INVALID_ARGUMENT_EXCEPTION;

import com.yello.server.domain.admin.repository.UserAdminRepository;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.user.dto.request.UserDataUpdateRequest;
import com.yello.server.domain.user.dto.request.UserDeleteReasonRequest;
import com.yello.server.domain.user.dto.request.UserDeviceTokenRequest;
import com.yello.server.domain.user.dto.request.UserUpdateRequest;
import com.yello.server.domain.user.dto.response.UserDataResponse;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserDetailV2Response;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.dto.response.UserSubscribeDetailResponse;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserData;
import com.yello.server.domain.user.entity.UserDataType;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.global.common.dto.EmptyObject;
import com.yello.server.global.common.util.ConstantUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
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
    private final UserAdminRepository userAdminRepository;
    private final UserDataRepository userDataRepository;
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

    @Transactional
    public void deleteUserWithReason(Long userId, UserDeleteReasonRequest request) {
        final User target = userRepository.getById(userId);
        target.delete();

        friendRepository.findAllByUserId(target.getId())
            .forEach(Friend::delete);

        friendRepository.findAllByTargetId(target.getId())
            .forEach(Friend::delete);

        cooldownRepository.findByUserId(target.getId())
            .ifPresent(Cooldown::delete);

        userDataRepository.save(UserData.of(WITHDRAW_REASON, request.value(), target));

    }

    @Transactional
    public void update(Long userId, UserUpdateRequest request) {
        // exception
        final User user = userRepository.getById(userId);
        final UserGroup userGroup = userGroupRepository.getById(request.groupId());
        final Gender gender = Gender.fromCode(request.gender());
        final Optional<UserData> userData = userDataRepository.findByUserIdAndTag(userId,
            UserDataType.ACCOUNT_UPDATED_AT);

        if (userData.isPresent()) {
            userData.get().setValue(
                ZonedDateTime.now(ConstantUtil.GlobalZoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            );
        } else {
            userDataRepository.save(UserData.of(
                ACCOUNT_UPDATED_AT,
                ZonedDateTime.now(ConstantUtil.GlobalZoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                user
            ));
        }

        // logic
        user.update(request, gender, userGroup);
    }

    public UserDataResponse readUserData(Long userId, UserDataType tag) {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<UserData> userData = userDataRepository.findByUserIdAndTag(userId, tag);

        return UserDataResponse.of(user, tag, userData.isPresent() ? userData.get().getValue() : null);
    }

    @Transactional
    public void updateUserData(Long userId, UserDataType tag, UserDataUpdateRequest request) {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<UserData> userData = userDataRepository.findByUserIdAndTag(userId, tag);
        final DateTimeFormatter zonedDateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String savedValue = null;

        if (tag.getClassType().equals(String.class)) {
            savedValue = request.value();
        } else if (tag.getClassType().equals(ZonedDateTime.class)) {
            try {
                savedValue = ZonedDateTime
                    .parse(request.value(), zonedDateFormatter)
                    .format(zonedDateFormatter);
            } catch (DateTimeParseException exception) {
                throw new UserException(USER_DATA_INVALID_ARGUMENT_EXCEPTION);
            }
        }

        if (userData.isPresent()) {
            userDataRepository.update(userId, tag, savedValue);
        } else {
            userDataRepository.save(UserData.of(tag, savedValue, user));
        }
    }
}
