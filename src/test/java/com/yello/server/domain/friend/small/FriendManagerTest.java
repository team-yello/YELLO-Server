package com.yello.server.domain.friend.small;

import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.friend.service.FriendManager;
import com.yello.server.domain.friend.service.FriendManagerImpl;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("FriendManager 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class FriendManagerTest {

    private final UserRepository userRepository = new FakeUserRepository();

    private FriendManager friendManager;

    @BeforeEach
    void init() {
        this.friendManager = new FriendManagerImpl(userRepository);
        generateUserForTest(10);
    }

    @Test
    void 온보딩_시_추천_친구_리스트_조회에_성공합니다() {
        // given
        List<String> friendKakaoIds = Arrays.asList("1", "2", "3");

        // when
        List<User> users = friendManager.getRecommendedFriendsForOnBoarding(friendKakaoIds);

        // then
        assertThat(users).hasSize(3);
        assertThat(users.get(0).getName()).isEqualTo("name1");
    }

    @Test
    void 온보딩_시_비어있는_추천_친구_리스트_조회에_성공합니다() {
        // given
        List<String> friendKakaoIds = Arrays.asList("99", "100", "101");

        // when
        List<User> users = friendManager.getRecommendedFriendsForOnBoarding(friendKakaoIds);

        // then
        assertThat(users).hasSize(0);
    }

    private void generateUserForTest(int count) {
        School school = School.builder()
            .schoolName("Test School")
            .departmentName("Testing")
            .build();

        for (long i = 0; i < count; i++) {
            userRepository.save(User.builder()
                .id((i + i))
                .recommendCount(0L)
                .name("name%d".formatted(i))
                .yelloId("yelloId%d".formatted(i))
                .gender(Gender.MALE)
                .point(200)
                .social(Social.KAKAO)
                .profileImage("test image")
                .uuid("%d".formatted(i))
                .deletedAt(null)
                .group(school)
                .groupAdmissionYear(20)
                .email("test%d@test.com".formatted(i))
                .build());
        }
    }
}
