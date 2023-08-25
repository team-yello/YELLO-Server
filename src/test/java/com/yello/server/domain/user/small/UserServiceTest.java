package com.yello.server.domain.user.small;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.cooldown.FakeCooldownRepository;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.infrastructure.redis.FakeTokenRepository;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import com.yello.server.util.TestDataRepositoryUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("UserService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserServiceTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final TokenRepository tokenRepository = new FakeTokenRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository
    );

    private UserService userService;

    @BeforeEach
    void init() {
        this.userService = UserService.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .voteRepository(voteRepository)
            .cooldownRepository(cooldownRepository)
            .tokenRepository(tokenRepository)
            .build();

        for (int i = 1; i <= 2; i++) {
            testDataUtil.generateUser(i, 1L);
        }
    }

    @Test
    void 내_정보_조회에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        final UserDetailResponse result = userService.findMyProfile(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("name1");
    }

    @Test
    void 다른_유저_정보_조회에_성공합니다() {
        // given
        final Long userId = 2L;

        // when
        final UserResponse result = userService.findUserById(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("name2");
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
        assertThat(user.getDeviceToken()).isNull();
    }
}