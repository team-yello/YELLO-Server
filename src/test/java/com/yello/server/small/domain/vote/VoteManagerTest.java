package com.yello.server.small.domain.vote;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.user.service.UserManager;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteManager;
import com.yello.server.domain.vote.service.VoteManagerImpl;
import com.yello.server.small.domain.friend.FakeFriendRepository;
import com.yello.server.small.domain.question.FakeQuestionRepository;
import com.yello.server.small.domain.user.FakeUserManager;
import com.yello.server.small.domain.user.FakeUserRepository;
import com.yello.server.util.TestDataUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class VoteManagerTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final TestDataUtil testDataUtil = new TestDataUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository
    );

    private final UserManager userManager = new FakeUserManager(userRepository);

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

        for (long i = 1; i <= 5; i++) {
            userData.add(testDataUtil.generateUser(i, 1L));
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
        assertThat(vote.getSender().getGender().getIntial()).isEqualTo("F");
    }

}
