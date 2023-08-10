package com.yello.server.small.domain.vote;

import static com.yello.server.global.common.ErrorCode.NOT_FOUND_VOTE_EXCEPTION;

import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import com.yello.server.domain.vote.repository.VoteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public class FakeVoteRepository implements VoteRepository {

    private final List<Vote> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public Vote save(Vote vote) {
        Vote newVote = Vote.builder()
            .id(vote.getId() == null ? id++ : vote.getId())
            .answer(vote.getAnswer())
            .nameHint(vote.getNameHint())
            .isAnswerRevealed(vote.getIsAnswerRevealed())
            .isRead(vote.getIsRead())
            .sender(vote.getSender())
            .receiver(vote.getReceiver())
            .question(vote.getQuestion())
            .colorIndex(vote.getColorIndex())
            .createdAt(vote.getCreatedAt())
            .build();
        data.add(newVote);
        return newVote;
    }

    @Override
    public Vote getById(Long id) {
        return data.stream()
            .filter(vote -> vote.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
    }

    @Override
    public Optional<Vote> findById(Long id) {
        return data.stream()
            .filter(vote -> vote.getId().equals(id))
            .findFirst();
    }

    @Override
    public Integer countAllByReceiverUserId(Long userId) {
        return data.stream()
            .filter(vote -> vote.getReceiver().getId().equals(userId))
            .toList()
            .size();
    }

    @Override
    public Integer countUnreadByReceiverUserId(Long userId) {
        return data.stream()
            .filter(vote -> vote.getReceiver().getId().equals(userId))
            .filter(vote -> !vote.getIsRead())
            .toList()
            .size();
    }

    @Override
    public List<Vote> findAllByReceiverUserId(Long userId, Pageable pageable) {
        return data.stream()
            .filter(vote -> vote.getReceiver().getId().equals(userId))
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
    }

    @Override
    public List<Vote> findAllReceivedByFriends(Long userId, Pageable pageable) {
        return data.stream()
            .filter(vote -> vote.getReceiver().getId().equals(userId))
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .toList();
    }

    @Override
    public Integer countAllReceivedByFriends(Long userId) {
        return data.stream()
            .filter(vote -> vote.getReceiver().getId().equals(userId))
            .toList()
            .size();
    }
}
