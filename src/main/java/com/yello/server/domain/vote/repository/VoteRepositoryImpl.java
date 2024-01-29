package com.yello.server.domain.vote.repository;

import static com.yello.server.domain.friend.entity.QFriend.friend;
import static com.yello.server.domain.vote.entity.QVote.vote;
import static com.yello.server.global.common.ErrorCode.NOT_FOUND_VOTE_EXCEPTION;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yello.server.domain.vote.entity.Vote;
import com.yello.server.domain.vote.exception.VoteNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteRepositoryImpl implements VoteRepository {

    private final VoteJpaRepository voteJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Vote save(Vote vote) {
        return voteJpaRepository.save(vote);
    }

    @Override
    public Vote getById(Long id) {
        return voteJpaRepository.findById(id)
            .orElseThrow(() -> new VoteNotFoundException(NOT_FOUND_VOTE_EXCEPTION));
    }

    @Override
    public Optional<Vote> findById(Long id) {
        return voteJpaRepository.findById(id);
    }

    @Override
    public Integer countAllByReceiverUserId(Long userId) {
        return voteJpaRepository.countAllByReceiverUserId(userId);
    }

    @Override
    public Integer countUnreadByReceiverUserId(Long userId) {
        return voteJpaRepository.countUnreadByReceiverUserId(userId);
    }

    @Override
    public Integer countUnreadByReceiverDeviceToken(String deviceToken) {
        return voteJpaRepository.countUnreadByReceiverDeviceToken(deviceToken);
    }

    @Override
    public List<Vote> findAllByReceiverUserId(Long userId, Pageable pageable) {
        return voteJpaRepository.findAllByReceiverUserId(userId, pageable);
    }

    @Override
    public List<Vote> findAllReceivedByFriends(Long userId, Pageable pageable) {
        return voteJpaRepository.findAllReceivedByFriends(userId, pageable);
    }

    @Override
    public Integer countAllReceivedByFriends(Long userId) {
        return voteJpaRepository.countAllReceivedByFriends(userId);
    }

    @Override
    public Integer countReadByReceiverUserId(Long userId) {
        return voteJpaRepository.countReadByReceiverUserId(userId);
    }

    @Override
    public Integer countOpenKeywordByReceiverUserId(Long userId) {
        return voteJpaRepository.countOpenKeywordByReceiverUserId(userId);
    }

    @Override
    public Integer countOpenNameByReceiverUserId(Long userId) {
        return voteJpaRepository.countOpenNameByReceiverUserId(userId);
    }

    @Override
    public Integer countOpenFullNameByReceiverUserId(Long userId) {
        return voteJpaRepository.countOpenFullNameByReceiverUserId(userId);
    }

    @Override
    public List<Vote> findUserSendReceivedByFriends(Long userId, Pageable pageable) {
        return jpaQueryFactory.selectFrom(vote)
            .where(vote.sender.id.eq(userId)
                .and(vote.receiver.id.in(
                    JPAExpressions
                        .select(friend.target.id)
                        .from(friend)
                        .where(friend.user.id.eq(userId)
                            .and(friend.deletedAt.isNull())
                        )))
                .and(vote.sender.deletedAt.isNull())
                .and(vote.receiver.deletedAt.isNull()))
            .orderBy(vote.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Long countUserSendReceivedByFriends(Long userId) {
        return jpaQueryFactory.select(vote.count())
            .from(vote)
            .where(vote.sender.id.eq(userId)
                .and(vote.receiver.id.in(
                    JPAExpressions
                        .select(friend.target.id)
                        .from(friend)
                        .where(friend.user.id.eq(userId)
                            .and(friend.deletedAt.isNull())
                        )))
                .and(vote.sender.deletedAt.isNull())
                .and(vote.receiver.deletedAt.isNull()))
            .fetchOne();
    }
}
