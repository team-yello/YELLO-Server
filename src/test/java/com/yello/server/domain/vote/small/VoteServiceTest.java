package com.yello.server.domain.vote.small;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.cooldown.FakeCooldownRepository;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.FakeFriendRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.UserGroupType;
import com.yello.server.domain.keyword.FakeKeywordRepository;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.repository.KeywordRepository;
import com.yello.server.domain.question.FakeQuestionGroupTypeRepository;
import com.yello.server.domain.question.FakeQuestionRepository;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionGroupType;
import com.yello.server.domain.question.repository.QuestionGroupTypeRepository;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.FakeUserManager;
import com.yello.server.domain.user.FakeUserRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.FakeVoteManager;
import com.yello.server.domain.vote.FakeVoteRepository;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.dto.response.RevealNameResponse;
import com.yello.server.domain.vote.dto.response.VoteAvailableResponse;
import com.yello.server.domain.vote.dto.response.VoteCreateVO;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteListResponse;
import com.yello.server.domain.vote.dto.response.VoteUnreadCountResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.infrastructure.rabbitmq.FakeMessageQueueRepository;
import com.yello.server.infrastructure.rabbitmq.FakeProducerService;
import com.yello.server.infrastructure.rabbitmq.service.ProducerService;
import com.yello.server.util.TestDataRepositoryUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

@DisplayName("VoteService 에서")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class VoteServiceTest {

    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final UserRepository userRepository = new FakeUserRepository(friendRepository);
    private final UserManager userManager = new FakeUserManager(userRepository);
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final QuestionGroupTypeRepository questionGroupTypeRepository = new FakeQuestionGroupTypeRepository(questionRepository);

    private final VoteManager voteManager = new FakeVoteManager(
        userRepository,
        questionRepository,
        voteRepository,
        friendRepository,
        userManager
    );
    private final TestDataRepositoryUtil testDataUtil = new TestDataRepositoryUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository,
        questionGroupTypeRepository
    );
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final KeywordRepository keywordRepository = new FakeKeywordRepository();
    private final ProducerService producerService =
        new FakeProducerService(new FakeMessageQueueRepository());

    private VoteService voteService;
    private List<Question> questionData = new ArrayList<>();
    private List<QuestionGroupType> questionGroupTypeData = new ArrayList<>();
    private List<User> userData = new ArrayList<>();

    @BeforeEach
    void init() {
        this.voteService = VoteService.builder()
            .voteRepository(voteRepository)
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .userRepository(userRepository)
            .questionRepository(questionRepository)
            .keywordRepository(keywordRepository)
            .producerService(producerService)
            .voteManager(voteManager)
            .questionGroupTypeRepository(questionGroupTypeRepository)
            .build();

        for (long i = 1; i <= 8; i++) {
            questionData.add(testDataUtil.generateQuestion(i));
        }

        for (long i = 1; i <= 8; i++) {
            QuestionGroupType questionGroupType = testDataUtil.generateQuestionGroupType(i, questionData.get(Long.valueOf(i).intValue() -1));
            questionGroupTypeData.add(questionGroupType);
        }


        for (long i = 1; i <= 5; i++) {
            userData.add(testDataUtil.generateUser(i, 1L, UserGroupType.UNIVERSITY));
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

    @AfterEach
    void cleanup() {
        questionData.clear();
        questionGroupTypeData.clear();
        userData.clear();
    }

    @Test
    void 내가_받은_투표_전체_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);

        // when
        VoteListResponse result = voteService.findAllVotes(userId, pageable);

        // then
        assertThat(result.totalCount()).isEqualTo(4);
        assertThat(result.votes().get(0).id()).isEqualTo(1L);
    }

    @Test
    void 읽지_않은_투표_개수_조회에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        final VoteUnreadCountResponse result = voteService.getUnreadVoteCount(userId);

        // then
        assertThat(result.totalCount()).isEqualTo(4);
    }

    @Test
    void 상세_투표_조회에_성공합니다() {
        // given
        final Long voteId = 1L;
        final Vote vote = voteRepository.getById(voteId);

        // when
        final VoteDetailResponse result = voteService.findVoteById(voteId, 1L);

        // then
        assertThat(result.senderName()).isEqualTo("name2");
        assertThat(vote.getIsRead()).isEqualTo(true);

    }

    @Test
    void 친구들이_받은_투표_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Pageable pageable = createPageable(0);

        // when
        VoteFriendResponse result =
            voteService.findAllFriendVotes(userId, pageable); // 다시 확인 !!

        // then
        assertThat(result.totalCount()).isEqualTo(4);
        assertThat(result.friendVotes().size()).isEqualTo(4);
        assertThat(result.friendVotes().get(0).id()).isEqualTo(1L);
    }

    @Test
    void 특정_투표에_대한_키워드_확인에_성공합니다() {
        // given
        final Long userId = 1L;
        final Long voteId = 1L;
        final Vote vote = voteRepository.getById(voteId);
        final User user = userRepository.getById(userId);
        final Integer beforePoint = user.getPoint();

        // when
        final KeywordCheckResponse result = voteService.checkKeyword(userId, voteId);

        // then
        assertThat(vote.getIsAnswerRevealed()).isEqualTo(true);
        assertThat(user.getPoint()).isEqualTo(beforePoint - 100);
        assertThat(result.answer()).isEqualTo("test");
    }

    @Test
    void 투표하기_시_투표_8개_조회에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        List<QuestionForVoteResponse> result = voteService.findVoteQuestionList(userId);

        // then
        assertThat(result.size()).isEqualTo(8);

    }

    @Test
    void 투표_가능_여부_조회에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        VoteAvailableResponse result = voteService.checkVoteAvailable(userId);

        // then
        assertThat(result.isPossible()).isEqualTo(true);

    }

    @Test
    void 투표_생성에_성공합니다() {
        // given
        final Long userId = 1L;

        final List<VoteAnswer> voteAnswerList = new ArrayList<>();
        VoteAnswer answer1 = VoteAnswer.builder()
            .friendId(2L)
            .questionId(1L)
            .keywordName("test")
            .colorIndex(0)
            .build();
        voteAnswerList.add(answer1);

        CreateVoteRequest request = CreateVoteRequest.builder()
            .voteAnswerList(voteAnswerList)
            .totalPoint(3)
            .build();

        // when
        VoteCreateVO result = voteService.createVote(userId, request);

        // then
        assertThat(result.point()).isEqualTo(2003);
        assertThat(result.votes().size()).isEqualTo(1);
    }

    @Test
    void 투표_이름_힌트_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Long voteId = 1L;
        final User user = userRepository.getById(userId);

        // when
        RevealNameResponse result = voteService.revealNameHint(userId, voteId);

        // then
        assertThat(result.name()).isIn('n', 'a');
        assertThat(result.nameIndex()).isLessThan(2);
    }
}
