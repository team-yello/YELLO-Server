package com.yello.server.small.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.small.cooldown.FakeCooldownRepository;
import com.yello.server.small.friend.FakeFriendRepository;
import com.yello.server.small.vote.FakeVoteRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private UserService userService;

    @BeforeEach
    void init() {
        this.userService = UserService.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .voteRepository(voteRepository)
            .cooldownRepository(cooldownRepository)
            .build();

        School school = School.builder()
            .schoolName("Test School")
            .departmentName("Testing")
            .build();
        userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("test")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image").uuid("1234")
            .deletedAt(null).group(school)
            .groupAdmissionYear(20).email("test@test.com")
            .build());
        userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("hello")
            .yelloId("helloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 2").uuid("5678")
            .deletedAt(null).group(school)
            .groupAdmissionYear(17).email("hello@test.com")
            .build());

    }

    @Test
    void 내_정보_조회에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        final UserDetailResponse result = userService.findMyProfile(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("test");
    }

    @Test
    void 다른_유저_정보_조회에_성공합니다() {
        // given
        final Long userId = 2L;

        // when
        final UserResponse result = userService.findUserById(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("hello");
    }

    @Test
    void 존재하지_않는_유저_조회_시_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 123123L;

        // when
        // then
        assertThatThrownBy(() -> userService.findUserById(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 유저_삭제에_성공합니다() {
        // given
        final Long userId = 1L;
        final User user = userRepository.getById(userId);

        // when
        userService.delete(user);

        // then
        assertThat(user.getPoint()).isZero();
        assertThat(user.getDeletedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}