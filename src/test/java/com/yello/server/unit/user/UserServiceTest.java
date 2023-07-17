package com.yello.server.unit.user;

import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.dto.UserResponse;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.service.UserServiceImpl;
import com.yello.server.domain.vote.entity.VoteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private VoteRepository voteRepository;

    @Test
    @DisplayName("유저 상세 조회에 성공합니다.")
    public void findUserTest() {
        // given
        Long userId = 1L;
        User user = createUser(userId, "test", "test@test.com");
        List<Friend> friends = new ArrayList<>();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(friendRepository.findAllByUser(user)).thenReturn(friends);
        when(voteRepository.getCountAllByReceiverUserId(userId)).thenReturn(0);

        // when
        UserResponse result = userService.findUser(userId);

        // then
        assertThat(user.getId()).isEqualTo(result.userId());
        assertThat(user.getName()).isEqualTo(result.name());
        assertThat(friends.size()).isEqualTo(result.friendCount());
        assertThat(result.yelloCount()).isEqualTo(0);

        verify(userRepository, times(1)).findById(userId);
        verify(friendRepository, times(1)).findAllByUser(user);
        verify(voteRepository, times(1)).getCountAllByReceiverUserId(userId);
    }

    @Test
    @DisplayName("유저가 존재하지 않는 경우 UserNotFoundException이 발생합니다.")
    public void findUserNotFoundExceptionTest() {
        // given
        Long userId = 1L;

        // when, then
        assertThatThrownBy(() -> userService.findByUserId(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(USERID_NOT_FOUND_USER_EXCEPTION.getMessage());
    }

    private User createUser(Long userId, String name, String email) {
        School school = School.builder()
            .schoolName("testSchool")
            .departmentName("testDepartment")
            .build();

        return User.builder()
            .id(userId)
            .recommendCount(0L)
            .name(name)
            .yelloId("yello_" + userId)
            .gender(Gender.MALE)
            .point(100)
            .social(Social.KAKAO)
            .uuid("uuid_" + userId)
            .deletedAt(null)
            .group(school)
            .groupAdmissionYear(18)
            .email(email)
            .build();
    }
}
