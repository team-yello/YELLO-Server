package com.yello.server.domain.user.service;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.entity.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final VoteRepository voteRepository;
    private final CooldownRepository cooldownRepository;

    public UserDetailResponse findMyProfile(Long userId) {
        User user = userRepository.findById(userId);
        Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        Integer friendCount = friendRepository.findAllByUserId(user.getId()).size();

        return UserDetailResponse.of(user, yelloCount, friendCount);
    }

    public UserResponse findUserById(Long userId) {
        User user = userRepository.findById(userId);
        Integer yelloCount = voteRepository.countAllByReceiverUserId(user.getId());
        Integer friendCount = friendRepository.findAllByUserId(user.getId()).size();

        return UserResponse.of(user, yelloCount, friendCount);
    }

    @Transactional
    public void delete(User user) {
        User target = userRepository.findById(user.getId());
        target.delete();

        friendRepository.findAllByUserId(target.getId())
            .forEach(Friend::delete);

        friendRepository.findAllByTargetId(target.getId())
            .forEach(Friend::delete);

        cooldownRepository.findByUserId(target.getId())
            .ifPresent(Cooldown::delete);
    }
}
