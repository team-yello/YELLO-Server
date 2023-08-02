package com.yello.server.small.vote;

import com.yello.server.domain.cooldown.repository.CooldownRepository;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.question.entity.QuestionRepository;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.small.cooldown.FakeCooldownRepository;
import com.yello.server.small.friend.FakeFriendRepository;
import com.yello.server.small.user.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VoteServiceTest {

    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final CooldownRepository cooldownRepository = new FakeCooldownRepository();
    private VoteService voteService;

    @BeforeEach
    void init() {
        this.voteService  = VoteService.builder()
                .voteRepository(voteRepository)
                .friendRepository(friendRepository)
                .cooldownRepository(cooldownRepository)
                .userRepository(userRepository)
                .build();

    }

    @Test
    void 투표리스트_조회에_성공합니다() {

    }

    @Test
    void 투표_갸능여부_조회에_성공합니다() {

    }

    @Test
    void 친구_목록_셔플에_성공합니다() {

    }

    @Test
    void 투표_생성에_성공합니다() {

    }


}
