package com.yello.server.domain.user.service;

import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.entity.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.vote.entity.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final VoteRepository voteRepository;
    private final CooldownRepository cooldownRepository;

    @Override
    public UserDetailResponse findUser(Long userId) {
        User user = getUser(userId);
        Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        Integer friendCount = friendRepository.findAllByUser(user).size();

        return UserDetailResponse.of(user, yelloCount, friendCount);
    }

    @Override
    public UserResponse findUserById(Long userId) {
        User user = getUser(userId);
        Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        Integer friendCount = friendRepository.findAllByUser(user).size();

        return UserResponse.of(user, yelloCount, friendCount);
    }

    @Override
    @Transactional
    public void delete(User user) {
        User target = getUser(user.getId());
        target.delete();

        friendRepository.findAllByUser(target)
            .forEach(Friend::delete);

        friendRepository.findAllByTarget(target)
            .forEach(Friend::renew);

        cooldownRepository.findByUser(target)
            .ifPresent(Cooldown::delete);
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USERID_NOT_FOUND_USER_EXCEPTION));
    }
}
