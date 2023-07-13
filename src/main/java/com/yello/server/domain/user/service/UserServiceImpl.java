package com.yello.server.domain.user.service;

import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.user.dto.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.vote.entity.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final VoteRepository voteRepository;

    @Override
    public UserResponse findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(NOT_FOUND_USER_EXCEPTION));

        Integer friendCount = friendRepository.findAllByUser(user)
                .size();
        Integer yelloCount = voteRepository.findAllByReceiverUserId(user.getId())
                .size();

        return UserResponse.of(user, friendCount, yelloCount);
    }

    public User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER_EXCEPTION));
    }

}
