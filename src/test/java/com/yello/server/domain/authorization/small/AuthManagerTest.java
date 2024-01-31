package com.yello.server.domain.authorization.small;

import static com.yello.server.domain.admin.entity.AdminConfigurationType.ACCESS_TOKEN_TIME;
import static com.yello.server.domain.admin.entity.AdminConfigurationType.REFRESH_TOKEN_TIME;
import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.admin.FakeAdminConfigurationRepository;
import com.yello.server.domain.admin.repository.AdminConfigurationRepository;
import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.domain.authorization.exception.NotSignedInException;
import com.yello.server.domain.authorization.service.AuthManager;
import com.yello.server.domain.authorization.service.AuthManagerImpl;
import com.yello.server.domain.authorization.service.TokenJwtProvider;
import com.yello.server.domain.authorization.service.TokenProvider;
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
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserDataRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataRepositoryUtil;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("AuthManager 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class AuthManagerTest {

    private final AdminConfigurationRepository adminConfigurationRepository = new FakeAdminConfigurationRepository();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final NoticeRepository noticeRepository = new FakeNoticeRepository();
    private final PurchaseRepository purchaseRepository = new FakePurchaseRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final QuestionGroupTypeRepository questionGroupTypeRepository = new FakeQuestionGroupTypeRepository(
        questionRepository);
    private final String secretKey = Base64.getEncoder().encodeToString(
        "keyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTest".getBytes());
    private final TestDataEntityUtil testDataEntityUtil = new TestDataEntityUtil();
    private final TokenProvider tokenProvider = new TokenJwtProvider(secretKey);
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
    private AuthManager authManager;

    @BeforeEach
    void init() {
        this.authManager = AuthManagerImpl.builder()
            .adminConfigurationRepository(adminConfigurationRepository)
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .userRepository(userRepository)
            .tokenProvider(tokenProvider)
            .build();

        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        // soft-deleted User
        testDataUtil.generateDeletedUser(0L, userGroup);

        for (int i = 1; i <= 3; i++) {
            testDataUtil.generateUser(i, userGroup);
        }

        adminConfigurationRepository.setConfigurations(ACCESS_TOKEN_TIME, String.valueOf(30L));
        adminConfigurationRepository.setConfigurations(REFRESH_TOKEN_TIME, String.valueOf(10080L));
    }

    @Test
    void uuid로_가입한_유저를_가져옵니다() {
        // given
        final String uuid = "1";

        // when
        final User user = authManager.getSignedInUserByUuid(uuid);

        // then
        assertThat(user.getName()).isEqualTo("name1");
    }

    @Test
    void uuid로_가입한_유저_조회에_실패합니다() {
        // given
        final String uuid = "wrong_uuid";

        // when
        // then
        assertThatThrownBy(() -> authManager.getSignedInUserByUuid(uuid))
            .isInstanceOf(NotSignedInException.class)
            .hasMessageContaining(NOT_SIGNIN_USER_EXCEPTION.getMessage());
    }

    @Test
    void 토큰을_등록합니다() {
        // given
        final User user = userRepository.getById(1L);

        // when
        final ServiceTokenVO serviceTokenVO = authManager.issueToken(user);

        // then
        assertThat(serviceTokenVO.accessToken()).isNotEmpty();
        assertThat(serviceTokenVO.refreshToken()).isNotEmpty();
    }

    @Test
    void 새로운_액세스_토큰을_등록합니다() {
        // given

        // when

        // then
    }

    @Test
    void 새로운_액세스_토큰_등록_시_시크릿_키_인증에_실패합니다() {
        // given
        // when
        // then
    }

    @Test
    void 새로운_액세스_토큰_등록_시_유저_조회에_실패합니다() {
        // given
        // when
        // then
    }

    @Test
    void 회원가입_요청_객체_검증에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void 회원가입_요청_객체가_이미_가입한_유저인_경우_실패합니다() {
        // given
        // when
        // then
    }

    @Test
    void 회원가입_요청_객체가_중복_yelloId인_경우_실패합니다() {
        // given
        // when
        // then
    }

    @Test
    void 유저_재가입에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void 탈퇴하지_않은_유저이거나_삭제_30일이_넘은_경우_재가입_처리를_진행하지_않습니다() {
        // given
        // when
        // then
    }
}
