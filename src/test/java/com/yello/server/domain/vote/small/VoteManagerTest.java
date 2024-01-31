package com.yello.server.domain.vote.small;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;

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
import com.yello.server.domain.user.FakeUserManager;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserDataRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.domain.vote.service.VoteManagerImpl;
import com.yello.server.util.TestDataEntityUtil;
import com.yello.server.util.TestDataRepositoryUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

@DisplayName("VoteManager 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class VoteManagerTest {

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
    private final UserManager userManager = new FakeUserManager(userRepository);
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
    private VoteManager voteManager;
    private List<Question> questionData = new ArrayList<>();
    private List<User> userData = new ArrayList<>();

    @BeforeEach
    void init() {
        this.voteManager = VoteManagerImpl.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .questionRepository(questionRepository)
            .voteRepository(voteRepository)
            .userManager(userManager)
            .build();

        for (long i = 1; i <= 8; i++) {
            questionData.add(testDataUtil.generateQuestion(i));
        }

        final UserGroup userGroup = testDataUtil.generateGroup(1L, UserGroupType.UNIVERSITY);
        for (long i = 1; i <= 5; i++) {
            userData.add(testDataUtil.generateUser(i, userGroup));
        }

        testDataUtil.generateFriend(userData.get(1), userData.get(0));
        testDataUtil.generateFriend(userData.get(0), userData.get(0));
        testDataUtil.generateFriend(userData.get(0), userData.get(2));
        testDataUtil.generateFriend(userData.get(0), userData.get(3));
        testDataUtil.generateFriend(userData.get(0), userData.get(4));

        for (int i = 1; i <= 4; i++) {
            testDataUtil.generateVote(i, userData.get(1), userData.get(0), questionData.get(i));
        }
    }

    @Test
    void 투표_생성에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void 투표_질문_리스트_생성에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void 투표_이름_힌트_사용에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void 투표_키워드_힌트_사용에_성공합니다() {
        // given
        // when
        // then
    }

    @Test
    void 첫_투표_생성_성공에_성공합니다() {
        // given
        final Pageable pageable = createPageable(0);
        final Long maleUserId = 1L;
        User user = userRepository.getById(maleUserId);

        // when
        voteManager.makeGreetingVote(user);

        // then
        List<Vote> votes = voteRepository.findAllByReceiverUserId(maleUserId, pageable);
        Vote vote = votes.get(4);
        assertThat(vote.getSender().getGender().getInitial()).isEqualTo("F");
    }

}
