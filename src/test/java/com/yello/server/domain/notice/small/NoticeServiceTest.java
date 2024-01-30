package com.yello.server.domain.notice.small;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroupType;
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
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.util.TestDataRepositoryUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("NoticeService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class NoticeServiceTest {
    private final NoticeRepository noticeRepository = new FakeNoticeRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionGroupTypeRepository
        questionGroupTypeRepository = new FakeQuestionGroupTypeRepository(questionRepository);
    private final PurchaseRepository purchaseRepository = new FakePurchaseRepository();
    private NoticeService noticeService;
    private User user1;
    private Notice notice;
    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository,
        questionGroupTypeRepository,
        purchaseRepository,
        noticeRepository
    );

    @BeforeEach
    void init() {
        this.noticeService = NoticeService.builder()
            .noticeRepository(noticeRepository)
            .userRepository(userRepository)
            .build();

        final User user = testDataUtil.generateUser(1L, 1L, UserGroupType.HIGH_SCHOOL);
        user.setSubscribe(Subscribe.ACTIVE);
        notice = testDataUtil.genereateNotice(1L);
        noticeRepository.save(notice);
    }

    @Test
    void 공지_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final NoticeType tag = NoticeType.NOTICE;
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // when
        final NoticeDataResponse noticeResponse = noticeService.findNotice(userId, tag);
        System.out.println(noticeResponse.title() + ", " + noticeResponse.isAvailable());

        // then
        assertThat(noticeResponse.isAvailable()).isTrue();
        assertEquals(true, now.isAfter(notice.getStartDate()));
        assertEquals(true, now.isBefore(notice.getEndDate()));

    }
}
