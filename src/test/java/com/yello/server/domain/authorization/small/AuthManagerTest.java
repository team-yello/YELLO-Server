package com.yello.server.domain.authorization.small;

import static com.yello.server.global.common.ErrorCode.NOT_SIGNIN_USER_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.purchase.FakePurchaseRepository;
import com.yello.server.domain.purchase.repository.PurchaseRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.infrastructure.redis.FakeTokenRepository;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
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

    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final String secretKey = Base64.getEncoder().encodeToString(
        "keyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTestkeyForTest".getBytes());
    private final TokenProvider tokenProvider = new TokenJwtProvider(secretKey);
    private final TokenRepository tokenRepository = new FakeTokenRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionGroupTypeRepository questionGroupTypeRepository = new FakeQuestionGroupTypeRepository(questionRepository);
    private final PurchaseRepository purchaseRepository = new FakePurchaseRepository();
    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository,
        questionGroupTypeRepository,
        purchaseRepository
    );
    private AuthManager authManager;

    @BeforeEach
    void init() {
        this.authManager = AuthManagerImpl.builder()
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .userRepository(userRepository)
            .tokenRepository(tokenRepository)
            .tokenProvider(tokenProvider)
            .build();

        // soft-deleted User
        testDataUtil.generateDeletedUser(0L, 1L, UserGroupType.UNIVERSITY);

        for (int i = 1; i <= 3; i++) {
            testDataUtil.generateUser(i, 1L, UserGroupType.UNIVERSITY);
        }
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
        final ServiceTokenVO serviceTokenVO = authManager.registerToken(user);

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
