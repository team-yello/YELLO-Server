package com.yello.server.domain.vote.service;

import static com.yello.server.domain.vote.common.WeightedRandomFactory.randomPoint;
import static com.yello.server.global.common.ErrorCode.DUPLICATE_VOTE_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.INVALID_VOTE_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_POINT_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.LACK_USER_EXCEPTION;
import static com.yello.server.global.common.factory.TimeFactory.minusTime;
import static com.yello.server.global.common.util.ConstantUtil.COOL_DOWN_TIME;
import static com.yello.server.global.common.util.ConstantUtil.KEYWORD_HINT_POINT;
import static com.yello.server.global.common.util.ConstantUtil.NAME_HINT_DEFAULT;
import static com.yello.server.global.common.util.ConstantUtil.NAME_HINT_POINT;
import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;
import static com.yello.server.global.common.util.ConstantUtil.VOTE_COUNT;

import com.yello.server.domain.cooldown.entity.Cooldown;
import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.dto.response.QuestionVO;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.dto.response.RevealNameResponse;
import com.yello.server.domain.vote.dto.response.VoteAvailableResponse;
import com.yello.server.domain.vote.dto.response.VoteCreateResponse;
import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteFriendResponse;
import com.yello.server.domain.vote.dto.response.VoteListResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.exception.VoteForbiddenException;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.domain.vote.repository.VoteRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

  private final UserRepository userRepository;
  private final FriendRepository friendRepository;
  private final QuestionRepository questionRepository;
  private final CooldownRepository cooldownRepository;
  private final VoteRepository voteRepository;

  public VoteListResponse findAllVotes(Long userId, Pageable pageable) {
    final Integer count = voteRepository.countAllByReceiverUserId(userId);
    final List<VoteResponse> votes = voteRepository.findAllByReceiverUserId(userId, pageable)
        .stream()
        .map(VoteResponse::of)
        .toList();
    return VoteListResponse.of(count, votes);
  }

  @Transactional
  public VoteDetailResponse findVoteById(Long id) {
    final Vote vote = voteRepository.findById(id);
    vote.read();
    return VoteDetailResponse.of(vote);
  }

  public List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable) {
    return voteRepository.findAllReceivedByFriends(userId, pageable)
        .stream()
        .map(VoteFriendResponse::of)
        .toList();
  }

  @Transactional
  public KeywordCheckResponse checkKeyword(Long userId, Long voteId) {
    final Vote vote = voteRepository.findById(voteId);
    final User user = userRepository.findById(userId);

    vote.checkKeyword();

    if (user.getPoint() < KEYWORD_HINT_POINT) {
      throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
    }

    user.minusPoint(KEYWORD_HINT_POINT);
    return KeywordCheckResponse.of(vote);
  }

  public List<QuestionForVoteResponse> findVoteQuestionList(Long userId) {
    final User user = userRepository.findById(userId);

    final List<Friend> friends = friendRepository.findAllByUserId(user.getId());
    if (friends.size() < RANDOM_COUNT) {
      throw new FriendException(LACK_USER_EXCEPTION);
    }

    final List<Question> questions = questionRepository.findAll();
    Collections.shuffle(questions);

    final List<Question> questionList = questions.stream()
        .limit(VOTE_COUNT)
        .toList();

    return questionList.stream()
        .map(question -> generateVoteQuestion(user, question))
        .toList();
  }

  public VoteAvailableResponse checkVoteAvailable(Long userId) {
    final User user = userRepository.findById(userId);
    final List<Friend> friends = friendRepository.findAllByUserId(user.getId());

    if (friends.size() < RANDOM_COUNT) {
      throw new FriendException(LACK_USER_EXCEPTION);
    }

    final Cooldown cooldown = cooldownRepository.findByUserId(user.getId())
        .orElse(Cooldown.of(user, minusTime(LocalDateTime.now(), COOL_DOWN_TIME)));

    return VoteAvailableResponse.of(user, cooldown);
  }

  @Transactional
  public VoteCreateResponse createVote(Long userId, CreateVoteRequest request) {
    final User sender = userRepository.findById(userId);

    final List<VoteAnswer> voteAnswerList = request.voteAnswerList();
    IntStream.range(0, voteAnswerList.size())
        .forEach(index -> {
          if (index > 0 && voteAnswerList.get(index - 1).questionId() == voteAnswerList.get(index)
              .questionId()) {
            throw new VoteForbiddenException(DUPLICATE_VOTE_EXCEPTION);
          }

          User receiver = userRepository.findById(voteAnswerList.get(index).friendId());
          Question question = questionRepository.findById(voteAnswerList.get(index).questionId());
          Vote newVote = Vote.createVote(voteAnswerList.get(index).keywordName(), sender, receiver,
              question, voteAnswerList.get(index).colorIndex());
          voteRepository.save(newVote);
        });

    final Optional<Cooldown> cooldown = cooldownRepository.findByUserId(sender.getId());
    if (cooldown.isEmpty()) {
      cooldownRepository.save(Cooldown.of(sender, LocalDateTime.now()));
    } else {
      cooldown.get().updateDate(LocalDateTime.now());
    }

    sender.plusPoint(request.totalPoint());
    return VoteCreateResponse.of(sender.getPoint());
  }

  @Transactional
  public RevealNameResponse revealNameHint(Long userId, Long voteId) {
    final User sender = userRepository.findById(userId);

    if (sender.getPoint() < NAME_HINT_POINT) {
      throw new VoteForbiddenException(LACK_POINT_EXCEPTION);
    }

    final Vote vote = voteRepository.findById(voteId);
    if (vote.getNameHint() != NAME_HINT_DEFAULT) {
      throw new VoteNotFoundException(INVALID_VOTE_EXCEPTION);
    }

    int randomIndex = (int) (Math.random() * 2);
    vote.checkNameIndexOf(randomIndex);
    sender.minusPoint(NAME_HINT_POINT);

    return RevealNameResponse.of(vote.getSender(), randomIndex);
  }

  private QuestionForVoteResponse generateVoteQuestion(User user, Question question) {
    final List<Keyword> keywordList = question.getKeywordList();
    Collections.shuffle(keywordList);

    return QuestionForVoteResponse.builder()
        .friendList(getShuffledFriends(user))
        .keywordList(getShuffledKeywords(question))
        .question(QuestionVO.of(question))
        .questionPoint(randomPoint())
        .build();
  }

  private List<FriendShuffleResponse> getShuffledFriends(User user) {
    final List<Friend> allFriend = friendRepository.findAllByUserId(user.getId());
    Collections.shuffle(allFriend);

    return allFriend.stream()
        .map(FriendShuffleResponse::of)
        .limit(RANDOM_COUNT)
        .toList();
  }

  private List<String> getShuffledKeywords(Question question) {
    final List<Keyword> keywordList = question.getKeywordList();
    Collections.shuffle(keywordList);

    return keywordList.stream()
        .map(Keyword::getKeywordName)
        .limit(RANDOM_COUNT)
        .toList();
  }
}