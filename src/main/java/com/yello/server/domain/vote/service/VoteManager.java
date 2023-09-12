package com.yello.server.domain.vote.service;

import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.keyword.dto.response.KeywordCheckResponse;
import com.yello.server.domain.question.dto.response.QuestionForVoteResponse;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.dto.request.VoteAnswer;
import com.yello.server.domain.vote.entity.Vote;
import java.util.List;

public interface VoteManager {

    List<Vote> createVotes(Long senderId, List<VoteAnswer> voteAnswers);

    List<QuestionForVoteResponse> generateVoteQuestion(User user, List<Question> questions);

    int useNameHint(User sender, Vote vote);

    KeywordCheckResponse useKeywordHint(User user, Vote vote);

    void makeGreetingVote(User user);

    List<FriendShuffleResponse> getShuffledFriends(User user);

}
