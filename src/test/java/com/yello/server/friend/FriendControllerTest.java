package com.yello.server.friend;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.entity.SchoolRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@SpringBootTest
@Transactional
class FriendControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    SchoolRepository schoolRepository;

    @Test
    public void addFriendTest() {
        //given
        School group = schoolRepository.save(new School("솝트", "서버", 32));
        User user = userRepository.save(new User(1L, "현정", "hj_p", Gender.FEMALE, 0, Social.KAKAO, "D", "dd", LocalDateTime.now(), group));
        User friend = userRepository.save(new User(2L, "세훈", "훈세", Gender.MALE, 0, Social.KAKAO, "D", "dd", LocalDateTime.now(), group));

        //when
        Friend follow = friendRepository.save(Friend.newFriend(friend, user));

        //then
        Assertions.assertThat(friend.getName()).isEqualTo(follow.getFollower().getName());
        Assertions.assertThat(user.getName()).isEqualTo(follow.getFollowing().getName());

    }

}