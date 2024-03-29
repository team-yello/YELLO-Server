package com.yello.server.infrastructure.firebase.small;

import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.FakeUserGroupRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.notice.FakeNoticeRepository;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.purchase.FakePurchaseRepository;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserDataRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.infrastructure.firebase.dto.request.NotificationMessage;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.firebase.manager.FCMManagerImpl;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataRepositoryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("FcmManager 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class FcmManagerTest {

    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final NoticeRepository noticeRepository = new FakeNoticeRepository();
    private final PurchaseRepository purchaseRepository = new FakePurchaseRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final QuestionGroupTypeRepository
        questionGroupTypeRepository = new FakeQuestionGroupTypeRepository(questionRepository);
    private final TestDataEntityUtil testDataEntityUtil = new TestDataEntityUtil();
    private final UserDataRepository userDataRepository = new FakeUserDataRepository();
    private final UserGroupRepository userGroupRepository = new FakeUserGroupRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        friendRepository,
        noticeRepository,
        purchaseRepository,
        questionGroupTypeRepository,
        questionRepository,
        testDataEntityUtil,
        userDataRepository,
        userGroupRepository,
        userRepository,
        voteRepository
    );
    private FCMManager fcmManager;

    @BeforeEach
    void init() {
        this.fcmManager = FCMManagerImpl.builder()
            .voteRepository(voteRepository)
            .build();

        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        User user = testDataUtil.generateUser(1L, userGroup);
        User target = testDataUtil.generateUser(2L, userGroup);
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
