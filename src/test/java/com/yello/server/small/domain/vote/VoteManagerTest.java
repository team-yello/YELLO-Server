package com.yello.server.small.domain.vote;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class VoteManagerTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();

    private final UserManager userManager = new FakeUserManager(userRepository);

    private VoteManager voteManager;

    @BeforeEach
    void init() {
        this.voteManager = VoteManagerImpl.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .questionRepository(questionRepository)
            .voteRepository(voteRepository)
            .userManager(userManager)
            .build();

        School school = School.builder()
            .schoolName("Yello School")
            .departmentName("Yello")
            .build();
        Question question1 = questionRepository.save(Question.builder()
            .id(1L)
            .nameHead(null).nameFoot("와")
            .keywordHead("멋진").keywordFoot("에서 놀고싶어")
            .build());
        Question question2 = questionRepository.save(Question.builder()
            .id(2L)
            .nameHead(null).nameFoot("와")
            .keywordHead("이쁜").keywordFoot("닮아보여")
            .build());
        Question question3 = questionRepository.save(Question.builder()
            .id(3L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build());
        Question question4 = questionRepository.save(Question.builder()
            .id(4L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build());
        Question question5 = questionRepository.save(Question.builder()
            .id(5L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build());
        Question question6 = questionRepository.save(Question.builder()
            .id(6L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build());
        Question question7 = questionRepository.save(Question.builder()
            .id(7L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build());
        Question question8 = questionRepository.save(Question.builder()
            .id(8L)
            .nameHead(null).nameFoot("와")
            .keywordHead(null).keywordFoot("을 가고싶어")
            .build());

        User user1 = userRepository.save(User.builder()
            .id(1L)
            .recommendCount(0L).name("yello")
            .yelloId("yelloId").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .subscribe(Subscribe.NORMAL)
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
            .subscribe(Subscribe.NORMAL)
            .build());
        User user3 = userRepository.save(User.builder()
            .id(3L)
            .recommendCount(0L).name("yello3")
            .yelloId("yelloId3").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image3").group(school)
            .deletedAt(null).uuid("4567")
            .groupAdmissionYear(20).email("yello3@gmail.com")
            .subscribe(Subscribe.NORMAL)
            .build());
        User user4 = userRepository.save(User.builder()
            .id(4L)
            .recommendCount(0L).name("yello4")
            .yelloId("yelloId4").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image4").group(school)
            .deletedAt(null).uuid("45678")
            .groupAdmissionYear(20).email("yello4@gmail.com")
            .subscribe(Subscribe.NORMAL)
            .build());
        User user5 = userRepository.save(User.builder()
            .id(5L)
            .recommendCount(0L).name("yello5")
            .yelloId("yelloId5").gender(Gender.FEMALE)
            .social(Social.KAKAO)
            .profileImage("yello image5").group(school)
            .deletedAt(null).uuid("456789")
            .groupAdmissionYear(20).email("yello5@gmail.com")
            .subscribe(Subscribe.NORMAL)
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
        final Long femaleUserId = 1L;
        User user = userRepository.getById(femaleUserId);

        // when
        voteManager.makeGreetingVote(user);

        // then
        List<Vote> votes = voteRepository.findAllByReceiverUserId(femaleUserId, pageable);
        Vote vote = votes.get(4);
        assertThat(vote.getSender().getGender().getIntial()).isEqualTo("M");
    }

}
