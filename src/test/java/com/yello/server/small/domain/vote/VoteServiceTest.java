package com.yello.server.small.domain.vote;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.repository.KeywordRepository;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
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
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.small.domain.cooldown.FakeCooldownRepository;
import com.yello.server.small.domain.friend.FakeFriendRepository;
import com.yello.server.small.domain.keyword.FakeKeywordRepository;
import com.yello.server.small.domain.question.FakeQuestionRepository;
import com.yello.server.small.domain.user.FakeUserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class VoteServiceTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final KeywordRepository keywordRepository = new FakeKeywordRepository();
    private VoteService voteService;
    private Question question1;
    private Question question2;
    private Question question3;
    private Question question4;
    private Question question5;
    private Question question6;
    private Question question7;
    private Question question8;
    private Question question9;
    private Question question10;


    @BeforeEach
    void init() {
        this.voteService = VoteService.builder()
            .voteRepository(voteRepository)
            .friendRepository(friendRepository)
            .cooldownRepository(cooldownRepository)
            .userRepository(userRepository)
            .questionRepository(questionRepository)
            .keywordRepository(keywordRepository)
            .build();

        School school = School.builder()
            .schoolName("Yello School")
            .departmentName("Yello")
            .build();
        question1 = Question.builder()
            .id(1L)
            .nameHead(null).nameFoot("와")
            .keywordHead("멋진").keywordFoot("에서 놀고싶어")
            .build();
        question2 = Question.builder()
            .id(2L)
            .nameHead(null).nameFoot("와")
            .keywordHead("이쁜").keywordFoot("닮아보여")
            .build();
        question3 = Question.builder()
            .id(3L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build();
        question4 = Question.builder()
            .id(4L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build();
        question5 = Question.builder()
            .id(5L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build();
        question6 = Question.builder()
            .id(6L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build();
        question7 = Question.builder()
            .id(7L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build();
        question8 = Question.builder()
            .id(8L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build();

        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);
        questionRepository.save(question4);
        questionRepository.save(question5);
        questionRepository.save(question6);
        questionRepository.save(question7);
        questionRepository.save(question8);

        User user1 = userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("yello")
            .yelloId("yelloId").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image").group(school)
            .deletedAt(null).uuid("123")
            .groupAdmissionYear(20).email("yello@gmail.com")
            .build());
        User user2 = userRepository.save(User.builder()
            .id(2L)
            .recommendCount(0L).name("yello2")
            .yelloId("yelloId2").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image2").group(school)
            .deletedAt(null).uuid("456")
            .groupAdmissionYear(20).email("yello2@gmail.com")
            .build());
        User user3 = userRepository.save(User.builder()
            .id(3L)
            .recommendCount(0L).name("yello3")
            .yelloId("yelloId3").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image3").group(school)
            .deletedAt(null).uuid("4567")
            .groupAdmissionYear(20).email("yello3@gmail.com")
            .build());
        User user4 = userRepository.save(User.builder()
            .id(4L)
            .recommendCount(0L).name("yello4")
            .yelloId("yelloId4").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image4").group(school)
            .deletedAt(null).uuid("45678")
            .groupAdmissionYear(20).email("yello4@gmail.com")
            .build());
        User user5 = userRepository.save(User.builder()
            .id(5L)
            .recommendCount(0L).name("yello5")
            .yelloId("yelloId5").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image5").group(school)
            .deletedAt(null).uuid("456789")
            .groupAdmissionYear(20).email("yello5@gmail.com")
            .build());

        friendRepository.save(Friend.createFriend(user2, user1));
        friendRepository.save(Friend.createFriend(user1, user2));
        friendRepository.save(Friend.createFriend(user1, user3));
        friendRepository.save(Friend.createFriend(user1, user4));
        friendRepository.save(Friend.createFriend(user1, user5));

        voteRepository.save(Vote.builder()
            .id(1L)
            .colorIndex(0).answer("test")
            .isRead(false).nameHint(-1).isAnswerRevealed(false)
            .sender(userRepository.getById(2L))
            .receiver(userRepository.getById(1L))
            .question(question1).createdAt(LocalDateTime.now())
            .build());
        voteRepository.save(Vote.builder()
            .id(2L)
            .colorIndex(0).answer("test2")
            .isRead(false).nameHint(-1).isAnswerRevealed(false)
            .sender(userRepository.getById(2L))
            .receiver(userRepository.getById(1L))
            .question(question2).createdAt(LocalDateTime.now().minusMinutes(1))
            .build());
        voteRepository.save(Vote.builder()
            .id(3L)
            .colorIndex(0).answer("test3")
            .isRead(false).nameHint(-1).isAnswerRevealed(false)
            .sender(userRepository.getById(2L))
            .receiver(userRepository.getById(1L))
            .question(question3).createdAt(LocalDateTime.now().minusMinutes(2))
            .build());
        voteRepository.save(Vote.builder()
            .id(3L)
            .colorIndex(0).answer("test3")
            .isRead(false).nameHint(-1).isAnswerRevealed(false)
            .sender(userRepository.getById(2L))
            .receiver(userRepository.getById(1L))
            .question(question3).createdAt(LocalDateTime.now().minusMinutes(2))
            .build());


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
        final VoteDetailResponse result = voteService.findVoteById(voteId);

        // then
        assertThat(result.senderName()).isEqualTo("yello2");
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
    void 투표하기_시_투표_10개_조회에_성공합니다() {
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
            .friendId(1L)
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
        assertThat(result.name()).isIn('y', 'e');
        assertThat(result.nameIndex()).isLessThan(2);

    }
}
