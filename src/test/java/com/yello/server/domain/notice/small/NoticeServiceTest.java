package com.yello.server.domain.notice.small;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.FakeUserGroupRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.notice.FakeNoticeRepository;
import com.yello.server.domain.notice.dto.NoticeDataResponse;
import com.yello.server.domain.notice.entity.Notice;
import com.yello.server.domain.notice.entity.NoticeType;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.notice.service.NoticeService;
import com.yello.server.domain.purchase.FakePurchaseRepository;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserDataRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataRepositoryUtil;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@DisplayName("NoticeService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class NoticeServiceTest {

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
    private NoticeService noticeService;

    @BeforeEach
    void init() {
        this.noticeService = NoticeService.builder()
            .noticeRepository(noticeRepository)
            .userRepository(userRepository)
            .build();

        ZonedDateTime fixedDateTime = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.of("Asia/Seoul"));
        try (MockedStatic<ZonedDateTime> topZonedDateTimeUtilMock = Mockito.mockStatic(ZonedDateTime.class)) {
            topZonedDateTimeUtilMock.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(fixedDateTime);
        }

        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        final User user = testDataUtil.generateUser(1L, userGroup);
        user.setSubscribe(Subscribe.ACTIVE);
        testDataUtil.generateNotice(1L, NoticeType.NOTICE, ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    @Test
    void 공지_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final NoticeType tag = NoticeType.NOTICE;

        final Optional<Notice> notice = noticeRepository.findTopNotice(tag);
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        // when
        final NoticeDataResponse noticeResponse = noticeService.findNotice(userId,
            String.valueOf(tag));

        // then
        assertThat(noticeResponse.isAvailable()).isTrue();
        Assertions.assertEquals(true, now.isAfter(notice.get().getStartDate()));
        Assertions.assertEquals(true, now.isBefore(notice.get().getEndDate()));
    }
}
