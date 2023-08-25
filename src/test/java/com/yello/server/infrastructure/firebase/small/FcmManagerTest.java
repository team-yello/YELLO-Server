package com.yello.server.infrastructure.firebase.small;

import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.firebase.manager.FCMManagerImpl;
import com.yello.server.util.TestDataRepositoryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("FcmManager 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class FcmManagerTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();

    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository
    );

    private FCMManager fcmManager;

    @BeforeEach
    void init() {
        this.fcmManager = FCMManagerImpl.builder()
            .voteRepository(voteRepository)
            .build();

        User user = testDataUtil.generateUser(1L, 1L);
        User target = testDataUtil.generateUser(2L, 1L);
        Question question = testDataUtil.generateQuestion(1L);
        testDataUtil.generateVote(1L, user, target, question);
    }

    @Test
    void path를_제외한_메세지_객체_생성에_성공합니다() {
        // given
        final String testDeviceToken = "testDeviceToken";
        final NotificationMessage notificationMessage = NotificationMessage.toVoteAvailableNotificationContent();

        // when
        // then
        fcmManager.createMessage(testDeviceToken, notificationMessage);
    }

    @Test
    void path를_포함한_메세지_객체_생성에_성공합니다() {
        // given
        final String path = "/api/v1/vote/1";
        final String testDeviceToken = "deviceToken#2";
        final NotificationMessage notificationMessage = NotificationMessage.toVoteAvailableNotificationContent();

        // when
        // then
        fcmManager.createMessage(testDeviceToken, notificationMessage, path);
    }

}
