package com.yello.server.domain.question.service;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.keyword.entity.Keyword;
import com.yello.server.domain.keyword.entity.KeywordRepository;
import com.yello.server.domain.question.dto.response.YelloFriend;
import com.yello.server.domain.question.dto.response.YelloKeyword;
import com.yello.server.domain.question.dto.response.YelloQuestion;
import com.yello.server.domain.question.dto.response.YelloVoteResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.question.entity.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.domain.user.exception.UserException;
import com.yello.server.global.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.yello.server.global.common.util.ConstantUtil.RANDOM_COUNT;
import static com.yello.server.global.common.util.ConstantUtil.VOTE_COUNT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YelloVoteServiceImpl implements YelloVoteService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final QuestionRepository questionRepository;
    private final KeywordRepository keywordRepository;

    @Override
    public List<YelloVoteResponse> findYelloVoteList(Long userId) {

        List<YelloVoteResponse> yelloVoteList = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER_EXCEPTION));

        List<Question> question = questionRepository.findAll();
        Collections.shuffle(question);

        List<Question> yelloQuestionList = question.stream()
                .limit(VOTE_COUNT).toList();

        yelloQuestionList.forEach(yello -> yelloVoteList.add(getVoteData(user, yello)));


        return yelloVoteList;
    }

    public YelloVoteResponse getVoteData(User user, Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return YelloVoteResponse.builder()
                .friendList(getFriendList(user))
                .keywordList(getKeywordList(question))
                .question(YelloQuestion.of(question))
                .questionPoint(question.getPoint())
                .build();
    }

    public List<YelloFriend> getFriendList(User user) {
        List<Friend> allFriend = friendRepository.findAllByUser(user);
        Collections.shuffle(allFriend);

        return allFriend.stream()
                .map(YelloFriend::of)
                .limit(RANDOM_COUNT)
                .collect(Collectors.toList());
    }

    public List<YelloKeyword> getKeywordList(Question question) {
        List<Keyword> keywordList = question.getKeywordList();
        Collections.shuffle(keywordList);

        return keywordList.stream()
                .map(YelloKeyword::of)
                .limit(RANDOM_COUNT)
                .collect(Collectors.toList());
    }
}
