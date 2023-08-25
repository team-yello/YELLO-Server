package com.yello.server.domain.user.small;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserJpaRepository;
import com.yello.server.domain.user.repository.UserRepositoryImpl;
import com.yello.server.util.TestDataEntityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class UserRepositoryTest {


    private final TestDataEntityUtil testDataEntityUtil = new TestDataEntityUtil();

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void save에_성공합니다() {
        // given
        final User user = testDataEntityUtil.generateUser(1L, 1L);

        // when
        given(userRepository.save(any(User.class)))
            .willReturn(user);

        User newUser = userRepository.save(user);

        // then
        assertThat(newUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void findById에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void getById에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findByUuid에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void getByUuid에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void existsByUuid에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findByYelloId에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void getByYelloId에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findByDeviceToken에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findAllByGroupId에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findAllByGroupContainingName에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findAllByOtherGroupContainingName에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findAllByGroupContainingYelloId에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void findAllByOtherGroupContainingYelloId에_성공합니다() {
        // given
        // when
        // then
    }
}
