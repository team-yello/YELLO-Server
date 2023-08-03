package com.yello.server.small.vote;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteListResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.small.cooldown.FakeCooldownRepository;
import com.yello.server.small.friend.FakeFriendRepository;
import com.yello.server.small.question.FakeQuestionRepository;
import com.yello.server.small.user.FakeUserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

public class VoteServiceTest {

  private final UserRepository userRepository = new FakeUserRepository();
  private final FriendRepository friendRepository = new FakeFriendRepository();
  private final VoteRepository voteRepository = new FakeVoteRepository();
  private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
  private final QuestionRepository questionRepository = new FakeQuestionRepository();
  private VoteService voteService;

  @BeforeEach
  void init() {
    this.voteService = VoteService.builder()
        .voteRepository(voteRepository)
        .friendRepository(friendRepository)
        .cooldownRepository(cooldownRepository)
        .userRepository(userRepository)
        .build();

    School school = School.builder()
        .schoolName("Yello School")
        .departmentName("Yello")
        .build();
    Question question1 = Question.builder()
        .id(1L)
        .nameHead(null).nameFoot("와")
        .keywordHead("멋진").keywordFoot("에서 놀고싶어")
        .build();
    Question question2 = Question.builder()
        .id(2L)
        .nameHead(null).nameFoot("와")
        .keywordHead("이쁜").keywordFoot("닮아보여")
        .build();
    Question question3 = Question.builder()
        .id(3L)
        .nameHead(null).nameFoot("와")
        .keywordHead(null).keywordFoot("을 가고싶어")
        .build();

    userRepository.save(User.builder()
        .id(1L)
        .recommendCount(0L).name("yello")
        .yelloId("yelloId").gender(Gender.FEMALE)
        .point(200).social(Social.KAKAO)
        .profileImage("yello image").group(school)
        .deletedAt(null).uuid("123")
        .groupAdmissionYear(20).email("yello@gmail.com")
        .build());
    userRepository.save(User.builder()
        .id(2L)
        .recommendCount(0L).name("yello2")
        .yelloId("yelloId2").gender(Gender.FEMALE)
        .point(200).social(Social.KAKAO)
        .profileImage("yello image2").group(school)
        .deletedAt(null).uuid("456")
        .groupAdmissionYear(20).email("yello2@gmail.com")
        .build());

    voteRepository.save(Vote.builder()
        .id(1L)
        .colorIndex(0).answer("test")
        .isRead(false).nameHint(-1).isAnswerRevealed(false)
        .sender(userRepository.findById(2L))
        .receiver(userRepository.findById(1L))
        .question(question1).createdAt(LocalDateTime.now())
        .build());
    voteRepository.save(Vote.builder()
        .id(2L)
        .colorIndex(0).answer("test2")
        .isRead(false).nameHint(-1).isAnswerRevealed(false)
        .sender(userRepository.findById(2L))
        .receiver(userRepository.findById(1L))
        .question(question2).createdAt(LocalDateTime.now().minusMinutes(1))
        .build());
    voteRepository.save(Vote.builder()
        .id(3L)
        .colorIndex(0).answer("test3")
        .isRead(false).nameHint(-1).isAnswerRevealed(false)
        .sender(userRepository.findById(2L))
        .receiver(userRepository.findById(1L))
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
    assertThat(result.totalCount()).isEqualTo(3);
    assertThat(result.votes().get(0).id()).isEqualTo(1L);

  }

  @Test
  void 상세_투표_조회에_성공합니다() {
    // given
    final Long voteId = 1L;
    final Vote vote = voteRepository.findById(voteId);

    // when
    final VoteDetailResponse result = voteService.findVoteById(voteId);

    // then
    assertThat(result.senderName()).isEqualTo("yello2");
    assertThat(vote.getIsRead()).isEqualTo(true);

  }

  @Test
  void 친구들이_받은_투표_조회에_성공합니다() {
    // given

    // when

    // then

  }

  @Test
  void 특정_투표에_대한_키워드_확인에_성공합니다() {
    // given
    final Long userId = 1L;
    final Long voteId = 1L;
    final Vote vote = voteRepository.findById(voteId);
    final User user = userRepository.findById(userId);
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

    // when

    // then

  }

  @Test
  void 투표_가능_여부_조회에_성공합니다() {
    // given

    // when

    // then

  }

  @Test
  void 투표_생성에_성공합니다() {
    // given

    // when

    // then

  }

  @Test
  void 투표_이름_힌트_조회에_성공합니다() {
    // given

    // when

    // then

  }


}
