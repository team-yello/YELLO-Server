package com.yello.server.domain.user.small;

import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.user.service.UserManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("UserManager 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class UserManagerTest {

    private final static String OFFICIAL_NAME = "옐로팀";
    private final static String OFFICIAL_MALE_ID = "yello_male";
    private final static String OFFICIAL_FEMALE_ID = "yello_female";

    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);

    private UserManager userManager;

    @BeforeEach
    void init() {
        this.userManager = new UserManagerImpl(userRepository);
    }

    @Test
    void 남성_유저인_경우_여성_공식_계정_유저_조회에_성공합니다() {
        // given
        final Gender newUserGender = Gender.MALE;
        generateUserForTest();

        // when
        User officialUser = userManager.getOfficialUser(newUserGender);

        // then
        assertThat(officialUser.getUuid()).isEqualTo(OFFICIAL_FEMALE_ID);
        assertThat(officialUser.getGender().getInitial()).isEqualTo("F");
    }

    @Test
    void 여성_유저인_경우_남성_공식_계정_유저_조회에_성공합니다() {
        // given
        final Gender newUserGender = Gender.FEMALE;
        generateUserForTest();

        // when
        User officialUser = userManager.getOfficialUser(newUserGender);

        // then
        assertThat(officialUser.getUuid()).isEqualTo(OFFICIAL_MALE_ID);
        assertThat(officialUser.getGender().getInitial()).isEqualTo("M");
    }

    @Test
    void 공식_계정_유저가_없는_경우_생성한_뒤에_조회를_진행합니다() {
        // given
        final Gender newUserGender = Gender.FEMALE;

        // when
        User officialUser = userManager.getOfficialUser(newUserGender);

        // then
        assertThat(officialUser.getUuid()).isEqualTo(OFFICIAL_MALE_ID);
        assertThat(officialUser.getGender().getInitial()).isEqualTo("M");
    }


    private void generateUserForTest() {
        UserGroup userGroup = UserGroup.builder()
            .groupName("Test School")
            .subGroupName("Testing")
            .build();

        Long[] ids = {1L, 2L};
        Gender[] genders = {Gender.MALE, Gender.FEMALE};
        String[] yelloIds = {"yello_male", "yello_female"};
        String[] uuids = {"yello_male", "yello_female"};

        for (int i = 0; i < ids.length; i++) {
            userRepository.save(User.builder()
                .id(ids[i])
                .recommendCount(0L)
                .name(OFFICIAL_NAME)
                .yelloId(yelloIds[i])
                .gender(genders[i])
                .point(200)
                .social(Social.KAKAO)
                .profileImage("test image")
                .uuid(uuids[i])
                .deletedAt(null)
                .group(userGroup)
                .groupAdmissionYear(20)
                .email("test@test.com")
                .build());
        }
    }

}
