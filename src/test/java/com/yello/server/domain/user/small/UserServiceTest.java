package com.yello.server.domain.user.small;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.cooldown.FakeCooldownRepository;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.FakeUserGroupRepository;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.notice.FakeNoticeRepository;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.purchase.FakePurchaseRepository;
import com.yello.server.domain.purchase.entity.Purchase;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserDataRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.dto.request.UserUpdateRequest;
import com.yello.server.domain.user.dto.response.UserDetailResponse;
import com.yello.server.domain.user.dto.response.UserDetailV2Response;
import com.yello.server.domain.user.dto.response.UserResponse;
import com.yello.server.domain.user.dto.response.UserSubscribeDetailResponse;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Subscribe;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserService;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataRepositoryUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("UserService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserServiceTest {

    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
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
    private UserService userService;

    @BeforeEach
    void init() {
        this.userService = UserService.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .voteRepository(voteRepository)
            .cooldownRepository(cooldownRepository)
            .purchaseRepository(purchaseRepository)
            .userGroupRepository(userGroupRepository)
            .userDataRepository(userDataRepository)
            .build();

        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        final UserGroup userGroup2 = testDataUtil.generateGroup(2L, UserGroupType.UNIVERSITY);
        for (int i = 1; i <= 2; i++) {
            final User user = testDataUtil.generateUser(i, userGroup);
            user.setSubscribe(Subscribe.ACTIVE);
        }
        final User user = userRepository.getById(1L);
        testDataUtil.generatePurchase(1L, user);
    }

    @Test
    void 내_정보_조회에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        final UserDetailResponse result = userService.findMyProfile(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("name1");
    }

    @Test
    void 내_정보_조회_V2에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        final UserDetailV2Response result = userService.getUserDetailV2(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("name1");
    }

    @Test
    void 다른_유저_정보_조회에_성공합니다() {
        // given
        final Long userId = 2L;

        // when
        final UserResponse result = userService.findUserById(userId);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo("name2");
    }

    @Test
    void 존재하지_않는_유저_조회_시_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 123123L;

        // when
        // then
        assertThatThrownBy(() -> userService.findUserById(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 유저_삭제에_성공합니다() {
        // given
        final Long userId = 1L;
        final User user = userRepository.getById(userId);

        // when
        userService.delete(user);

        // then
        assertThat(user.getPoint()).isZero();
        assertThat(user.getDeletedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(user.getDeviceToken()).isNull();
    }

    @Test
    void 유저_구독_정보_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final User user = userRepository.getById(userId);
        final Long purchaseId = 1L;
        final Optional<Purchase> purchase = purchaseRepository.findById(purchaseId);
        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // when
        final UserSubscribeDetailResponse response = userService.getUserSubscribe(userId);

        // then
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.subscribe()).isEqualTo(Subscribe.ACTIVE.getInitial());
        assertThat(response.expiredDate()).isEqualTo(
            purchase.get()
                .getUpdatedAt()
                .plusDays(7)
                .format(pattern)
        );
    }

    @Test
    void 유저_정보_수정에_성공합니다() {
        // given
        final Long userId = 1L;
        final User user = userRepository.getById(userId);
        UserUpdateRequest request = UserUpdateRequest.builder()
            .name("after")
            .yelloId("afterId")
            .email("after@yello.com")
            .gender("M")
            .profileImageUrl("https://after.com")
            .groupId(2L)
            .groupAdmissionYear(24)
            .build();

        // when
        userService.update(userId, request);

        // then
        assertThat(user.getName()).isEqualTo(request.name());
        assertThat(user.getYelloId()).isEqualTo(request.yelloId());
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getGender()).isEqualTo(Gender.fromCode(request.gender()));
        assertThat(user.getProfileImage()).isEqualTo(request.profileImageUrl());
        assertThat(user.getGroup().getId()).isEqualTo(request.groupId());
        assertThat(user.getGroupAdmissionYear()).isEqualTo(request.groupAdmissionYear());
    }
}