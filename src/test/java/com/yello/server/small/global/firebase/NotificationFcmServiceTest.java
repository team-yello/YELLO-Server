package com.yello.server.small.global.firebase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.infrastructure.firebase.dto.request.NotificationRequest;
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
    private User user;
    private NotificationService notificationService;
    private NotificationRequest notificationRequest;

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
    }

    @Test
    void 푸시_알림_전송에_성공합니다() {
        // given
        tokenRepository.setDeviceToken(user.getUuid(), "test-device-token");
        notificationRequest = NotificationRequest.builder()
            .targetId(1L)
            .title("Test Notification")
            .message("Test message")
            .build();

        // when
        // then
        notificationService.sendNotification(notificationRequest);
    }

    @Test
    void 푸시_알림_전송_시_존재하지_않는_유저인_경우에_UserNotFoundException이_발생합니다() {
        // given
        tokenRepository.setDeviceToken(user.getUuid(), "test-device-token");
        notificationRequest = NotificationRequest.builder()
            .targetId(999L)
            .title("Test Notification")
            .message("Test message")
            .build();

        // when
        // then
        assertThatThrownBy(() -> notificationService.sendNotification(notificationRequest))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 푸시_알림_전송_시_UUID에_대한_DeviceToken이_없는_경우_RedisNotFoundException이_발생합니다() {
        // given
        notificationRequest = NotificationRequest.builder()
            .targetId(1L)
            .title("Test Notification")
            .message("Test message")
            .build();

        // when
        // then
        assertThatThrownBy(() -> notificationService.sendNotification(notificationRequest))
            .isInstanceOf(RedisNotFoundException.class)
            .hasMessageContaining("[RedisNotFoundException] uuid에 해당하는 디바이스 토큰 정보를 찾을 수 없습니다.");

    }

}