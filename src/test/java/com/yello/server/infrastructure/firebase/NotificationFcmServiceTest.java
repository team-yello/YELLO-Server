package com.yello.server.infrastructure.firebase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.firebase.service.NotificationFcmService;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("NotificationFcmService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationFcmServiceTest {

    private final FCMManager fcmManager = new FakeFcmManger();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);
    private NotificationService notificationService;

    private User user;
    private User target;
    private User dummy;
    private Vote vote;
    private Question question;
    @BeforeEach
    void init() {
        this.notificationService = NotificationFcmService.builder()
            .userRepository(userRepository)
            .fcmManager(fcmManager)
            .build();
        UserGroup userGroup = UserGroup.builder()
            .groupName("Test School")
            .subGroupName("Testing")
            .build();
        user = userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("test")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image").uuid("1234")
            .deletedAt(null).group(userGroup)
            .groupAdmissionYear(20).email("test@test.com")
            .build());
        target = userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("hello")
            .yelloId("helloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 2").uuid("5678")
            .deletedAt(null).group(userGroup)
            .groupAdmissionYear(17).email("hello@test.com")
            .build());
        dummy = User.builder()
            .id(3L)
            .recommendCount(0L).name("yello")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 3").uuid("91011")
            .deletedAt(null).group(userGroup)
            .groupAdmissionYear(19).email("yello@test.com")
            .build();
        question = Question.builder()
                .id(1L)
                .nameHead(null).nameFoot("와")
                .keywordHead("멋진").keywordFoot("에서 놀고싶어")
                .build();
        vote = Vote.builder()
                .id(1L)
                .colorIndex(0).answer("test")
                .isRead(false).nameHint(-1).isAnswerRevealed(false)
                .sender(userRepository.getById(1L))
                .receiver(userRepository.getById(2L))
                .question(question).createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 쪽지_받음_푸시_알림_전송에_성공합니다() {
        // given
        target.setDeviceToken("test-device-token");
        Question question = Question.builder()
            .id(1L)
            .nameHead(null).nameFoot("와")
            .keywordHead("멋진").keywordFoot("에서 놀고싶어")
            .build();
        Vote vote = Vote.builder()
            .id(1L)
            .colorIndex(0).answer("test")
            .isRead(false).nameHint(-1).isAnswerRevealed(false)
            .sender(userRepository.getById(1L))
            .receiver(userRepository.getById(2L))
            .question(question).createdAt(LocalDateTime.now())
            .build();

        // when
        // then
        notificationService.sendYelloNotification(vote);
    }

    @Test
    void 투표_가능_푸시_알림_전송에_성공합니다() {
        // given
        target.setDeviceToken("test-device-token");

        // when
        // then
        notificationService.sendVoteAvailableNotification(target.getId());
    }

    @Test
    void 친구_푸시_알림_전송에_성공합니다() {
        // given
        target.setDeviceToken("test-device-token");
        Friend friend = Friend.createFriend(user, target);

        // when
        // then
        notificationService.sendFriendNotification(friend);
    }

    @Test
    void 친구가_내가_보낸_쪽지_열람시_알림_전송에_성공합니다() {
        //given
        target.setDeviceToken("test-device-token");

        // when
        // then
        notificationService.sendOpenVoteNotification(vote.getSender());
    }

    @Test
    void 추천인_코드_가입하여_열람권_얻은_경우_알림_전송에_성공합니다() {
        //given
        target.setDeviceToken("test-device-token");

        // when
        // then
        notificationService.sendRecommendSignupAndGetTicketNotification(target);
    }

    @Test
    void 푸시_알림_전송_시_존재하지_않는_유저인_경우에_UserNotFoundException이_발생합니다() {
        // given
        target.setDeviceToken("test-device-token");

        // when
        // then
        assertThatThrownBy(() -> notificationService.sendVoteAvailableNotification(dummy.getId()))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }
}