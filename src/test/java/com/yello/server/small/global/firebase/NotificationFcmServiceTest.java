package com.yello.server.small.global.firebase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.firebase.service.NotificationFcmService;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import com.yello.server.infrastructure.redis.exception.RedisNotFoundException;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import com.yello.server.small.domain.user.FakeUserRepository;
import com.yello.server.small.global.redis.FakeTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationFcmServiceTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final TokenRepository tokenRepository = new FakeTokenRepository();
    private final FCMManager fcmManager = new FakeFcmManger();

    private NotificationService notificationService;
    private NotificationMessage notificationMessage;

    private User user;
    private User target;
    private User dummy;

    @BeforeEach
    void init() {
        this.notificationService = NotificationFcmService.builder()
            .userRepository(userRepository)
            .tokenRepository(tokenRepository)
            .fcmManager(fcmManager)
            .build();
        School school = School.builder()
            .schoolName("Test School")
            .departmentName("Testing")
            .build();
        user = userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("test")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image").uuid("1234")
            .deletedAt(null).group(school)
            .groupAdmissionYear(20).email("test@test.com")
            .build());
        target = userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("hello")
            .yelloId("helloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 2").uuid("5678")
            .deletedAt(null).group(school)
            .groupAdmissionYear(17).email("hello@test.com")
            .build());
        dummy = User.builder()
            .id(3L)
            .recommendCount(0L).name("yello")
            .yelloId("yelloworld").gender(Gender.MALE)
            .point(200).social(Social.KAKAO)
            .profileImage("test image 3").uuid("91011")
            .deletedAt(null).group(school)
            .groupAdmissionYear(19).email("yello@test.com")
            .build();
    }

    @Test
    void 쪽지_받음_푸시_알림_전송에_성공합니다() {
        // given
        tokenRepository.setDeviceToken(target.getUuid(), "test-device-token");
        notificationMessage = NotificationMessage.toYelloNotificationContent(user, "/dummy");

        // when
        notificationService.sendNotification(target, notificationMessage);

        // then
        assertThat(notificationMessage.title()).isEqualTo("남학생이 쪽지를 보냈어요!");
        assertThat(notificationMessage.message()).isEqualTo("나는 너가 ...");
        assertThat(notificationMessage.path()).isEqualTo("/dummy");
    }

    @Test
    void 투표_가능_푸시_알림_전송에_성공합니다() {
        // given
        tokenRepository.setDeviceToken(target.getUuid(), "test-device-token");
        notificationMessage = NotificationMessage.toVoteAvailableNotificationContent();

        // when
        notificationService.sendNotification(target, notificationMessage);

        // then
        assertThat(notificationMessage.title()).isEqualTo("친구에게 쪽지 보내고 포인트 받기");
        assertThat(notificationMessage.message()).isEqualTo("대기시간이 다 지났어요. 친구들에게 투표해봐요!");
    }

    @Test
    void 친구_푸시_알림_전송에_성공합니다() {
        // given
        tokenRepository.setDeviceToken(target.getUuid(), "test-device-token");
        notificationMessage = NotificationMessage.toFriendNotificationContent(user);

        // when
        notificationService.sendNotification(target, notificationMessage);

        // then
        assertThat(notificationMessage.title()).isEqualTo("test님이 회원님과 친구가 되었어요");
        assertThat(notificationMessage.message()).isEqualTo("친구와 쪽지를 주고받아 보세요!");
    }

    @Test
    void 푸시_알림_전송_시_존재하지_않는_유저인_경우에_UserNotFoundException이_발생합니다() {
        // given
        tokenRepository.setDeviceToken(target.getUuid(), "test-device-token");
        notificationMessage = NotificationMessage.toYelloNotificationContent(user, "/path");

        // when
        // then
        assertThatThrownBy(() -> notificationService.sendNotification(dummy, notificationMessage))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 푸시_알림_전송_시_UUID에_대한_DeviceToken이_없는_경우_RedisNotFoundException이_발생합니다() {
        // given
        notificationMessage = NotificationMessage.toYelloNotificationContent(user, "/path");

        // when
        // then
        assertThatThrownBy(() -> notificationService.sendNotification(target, notificationMessage))
            .isInstanceOf(RedisNotFoundException.class)
            .hasMessageContaining("[RedisNotFoundException] uuid에 해당하는 디바이스 토큰 정보를 찾을 수 없습니다.");

    }

}