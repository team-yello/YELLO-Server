package com.yello.server.domain.authorization.small;

import static com.yello.server.domain.admin.entity.AdminConfigurationType.ACCESS_TOKEN_TIME;
import static com.yello.server.domain.admin.entity.AdminConfigurationType.REFRESH_TOKEN_TIME;
import static com.yello.server.global.common.ErrorCode.GROUPID_NOT_FOUND_GROUP_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.UUID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_CONFLICT_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_REQUIRED_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.admin.FakeAdminConfigurationRepository;
import com.yello.server.domain.admin.repository.AdminConfigurationRepository;
import com.yello.server.domain.authorization.FakeAuthManager;
import com.yello.server.domain.authorization.FakeConnectionManager;
import com.yello.server.domain.authorization.dto.request.OnBoardingFriendRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriend;
import com.yello.server.domain.authorization.dto.response.OnBoardingFriendResponse;
import com.yello.server.domain.authorization.dto.response.SignUpResponse;
import com.yello.server.domain.authorization.exception.AuthBadRequestException;
import com.yello.server.domain.authorization.service.AuthManager;
import com.yello.server.domain.authorization.service.AuthService;
import com.yello.server.domain.authorization.service.TokenJwtProvider;
import com.yello.server.domain.authorization.service.TokenProvider;
import com.yello.server.domain.cooldown.FakeCooldownRepository;
import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.FakeFriendManager;
import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.friend.service.FriendManager;
import com.yello.server.domain.group.FakeUserGroupRepository;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.group.exception.GroupNotFoundException;
import com.yello.server.domain.group.repository.UserGroupRepository;
import com.yello.server.domain.notice.FakeNoticeRepository;
import com.yello.server.domain.notice.repository.NoticeRepository;
import com.yello.server.domain.purchase.FakePurchaseRepository;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserManager;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserConflictException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.FakeVoteManager;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.global.common.factory.PaginationFactory;
import com.yello.server.global.common.manager.ConnectionManager;
import com.yello.server.infrastructure.firebase.FakeFcmManger;
import com.yello.server.infrastructure.firebase.manager.FCMManager;
import com.yello.server.infrastructure.firebase.service.NotificationFcmService;
import com.yello.server.infrastructure.firebase.service.NotificationService;
import com.yello.server.infrastructure.rabbitmq.FakeMessageQueueRepository;
import com.yello.server.infrastructure.rabbitmq.repository.MessageQueueRepository;
import com.yello.server.infrastructure.redis.FakeTokenRepository;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import com.yello.server.util.TestDataRepositoryUtil;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

@DisplayName("AuthService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class AuthServiceTest {

    private final AdminConfigurationRepository adminConfigurationRepository = new FakeAdminConfigurationRepository();
    private final ConnectionManager connectionManager = new FakeConnectionManager();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final FCMManager fcmManager = new FakeFcmManger();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final MessageQueueRepository messageQueueRepository = new FakeMessageQueueRepository();
    private final NoticeRepository noticeRepository = new FakeNoticeRepository();
    private final PurchaseRepository purchaseRepository = new FakePurchaseRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final QuestionGroupTypeRepository
        questionGroupTypeRepository = new FakeQuestionGroupTypeRepository(questionRepository);
    private final String secretKey = Base64.getEncoder().encodeToString(
        "keyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTest".getBytes());
    private final TokenProvider tokenProvider = new TokenJwtProvider(secretKey);
    private final TokenRepository tokenRepository = new FakeTokenRepository();
    private final UserGroupRepository userGroupRepository = new FakeUserGroupRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);
    private final UserManager userManager = new FakeUserManager(userRepository);
    private final FriendManager friendManager = new FakeFriendManager(userRepository);
    private final NotificationService notificationService = NotificationFcmService.builder()
        .userRepository(userRepository)
        .fcmManager(fcmManager)
        .build();
    private final AuthManager authManager = new FakeAuthManager(
        adminConfigurationRepository, cooldownRepository, friendRepository, tokenProvider, userRepository
    );
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository,
        questionGroupTypeRepository,
        purchaseRepository,
        noticeRepository
    );
    private final VoteManager voteManager = new FakeVoteManager(
        userRepository, questionRepository, voteRepository, friendRepository,
        userManager);
    private AuthService authService;

    @BeforeEach
    void init() {
        this.authService = AuthService.builder()
            .userRepository(userRepository)
            .userGroupRepository(userGroupRepository)
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .messageQueueRepository(messageQueueRepository)
            .authManager(authManager)
            .friendManager(friendManager)
            .connectionManager(connectionManager)
            .voteManager(voteManager)
            .notificationService(notificationService)
            .build();

        userGroupRepository.save(testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY));

        // soft-deleted User
        testDataUtil.generateDeletedUser(0L, 1L, UserGroupType.UNIVERSITY);

        for (int i = 1; i <= 3; i++) {
            testDataUtil.generateUser(i, 1L, UserGroupType.UNIVERSITY);
        }

        adminConfigurationRepository.setConfigurations(ACCESS_TOKEN_TIME, String.valueOf(30L));
        adminConfigurationRepository.setConfigurations(REFRESH_TOKEN_TIME, String.valueOf(10080L));
    }

    @Test
    void Yello_Id_중복_조회에_성공합니다_중복임() {
        // given
        String yelloId = "yelloId1";

        // when
        Boolean isDuplicated = authService.isYelloIdDuplicated(yelloId);

        // then
        assertThat(isDuplicated).isEqualTo(true);
    }

    @Test
    void Yello_Id_중복_조회에_성공합니다_중복아님() {
        // given
        String yelloId = "hj_p__123123";

        // when
        Boolean isDuplicated = authService.isYelloIdDuplicated(yelloId);

        // then
        assertThat(isDuplicated).isEqualTo(false);
    }

    @Test
    void Yello_Id가_NULL일시_AuthBadRequestException이_발생합니다() {
        // given

        // when

        // then
        assertThatThrownBy(() -> authService.isYelloIdDuplicated(null))
            .isInstanceOf(AuthBadRequestException.class)
            .hasMessageContaining(YELLOID_REQUIRED_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_신규_유저_등록에_성공합니다() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("123456789")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(1L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("agenda511")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when
        SignUpResponse signUpUser = authService.signUp(request);
        final User expectedUser = userRepository.getByYelloId(signUpUser.yelloId());

        // then
        assertThat(signUpUser.yelloId()).isEqualTo(expectedUser.getYelloId());
    }

    @Test
    void 회원가입_신규_유저_등록에_실패합니다_uuid_충돌() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("1")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(1L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("agenda511")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when

        // then
        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(UserConflictException.class)
            .hasMessageContaining(UUID_CONFLICT_USER_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_신규_유저_등록에_실패합니다_yelloId_충돌() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("12345678")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(1L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("yelloId2")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when

        // then
        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(UserConflictException.class)
            .hasMessageContaining(YELLOID_CONFLICT_USER_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_신규_유저_등록에_실패합니다_존재하지_않는_group() {
        // given
        final SignUpRequest request = SignUpRequest.builder()
            .social(Social.KAKAO)
            .uuid("12345678")
            .email("agenda511@kakao.com")
            .profileImage("NO_IMAGE")
            .groupId(2L)
            .groupAdmissionYear(19)
            .name("이의제")
            .yelloId("2")
            .gender(Gender.MALE)
            .friends(new ArrayList<>())
            .recommendId("")
            .build();

        // when

        // then
        assertThatThrownBy(() -> authService.signUp(request))
            .isInstanceOf(GroupNotFoundException.class)
            .hasMessageContaining(GROUPID_NOT_FOUND_GROUP_EXCEPTION.getMessage());
    }

    @Test
    void 회원가입_친구_추천에_성공합니다_추천수() {
        // given
        String recommendYelloId = "yelloId1";
        String userYelloId = "yelloId2";

        // when
        final Long before = userRepository.getByYelloId(recommendYelloId).getRecommendCount();
        authService.recommendUser(recommendYelloId, userYelloId);
        final User after = userRepository.getByYelloId(recommendYelloId);

        // then
        assertThat(after.getRecommendCount()).isEqualTo(before + 1L);
    }

    @Test
    void 회원가입_친구_추천에_성공합니다_쿨다운삭제() {
        // given
        String recommendYelloId = "yelloId1";
        String userYelloId = "yelloId2";
        final User before = userRepository.getByYelloId(recommendYelloId);
        cooldownRepository.save(Cooldown.builder()
            .user(before)
            .build());

        // when
        authService.recommendUser(recommendYelloId, userYelloId);
        final User after = userRepository.getByYelloId(recommendYelloId);
        final Optional<Cooldown> cooldown = cooldownRepository.findByUserId(after.getId());

        // then
        assertThat(cooldown.isEmpty()).isEqualTo(true);
    }

    @Test
    void 회원가입_친구_등록에_성공합니다() {
        // given
        final Long id = 1L;
        final User user = userRepository.getById(id);
        List<Long> friendList = new ArrayList<>();
        friendList.add(2L);
        friendList.add(3L);

        // when

        authService.makeFriend(user, friendList);

        final List<Long> targetId = friendRepository.findAllByUserId(user.getId())
            .stream()
            .map((friend) -> friend.getTarget().getId())
            .toList();
        final List<Long> userId = friendRepository.findAllByTargetId(user.getId())
            .stream()
            .map((friend) -> friend.getUser().getId())
            .toList();

        // then
        assertThat(friendList).isSubsetOf(targetId);
        assertThat(friendList).isSubsetOf(userId);
    }

    @Test
    void 회원가입_추천친구목록_조회에_성공합니다() {
        // given
        List<String> kakaoFriendList = new ArrayList<>();
        kakaoFriendList.add("0"); // soft-deleted user
        kakaoFriendList.add("1");
        kakaoFriendList.add("2");

        final List<Long> expectedList = Stream.of("0", "1", "2")
            .map(userRepository::getByUuid)
            .map(User::getId)
            .toList();

        final OnBoardingFriendRequest request = OnBoardingFriendRequest.builder()
            .friendKakaoId(kakaoFriendList)
            .build();
        final Pageable pageable = PaginationFactory.createPageable(0);

        // when
        final OnBoardingFriendResponse response =
            authService.findOnBoardingFriends(request, pageable);

        // then
        assertThat(response.totalCount() - 1).isEqualTo(2);
        assertThat(expectedList.stream()
            .sorted()
            .toList())
            .isEqualTo(response.friendList()
                .stream()
                .map(OnBoardingFriend::id)
                .sorted()
                .toList());
    }
}
