package com.yello.server.domain.user.repository;

import static com.yello.server.global.common.ErrorCode.USER_POST_COMMENT_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USER_POST_NOT_FOUND_EXCEPTION;

import com.yello.server.domain.user.entity.UserPost;
import com.yello.server.domain.user.entity.UserPostComment;
import com.yello.server.domain.user.entity.UserPostCommentStatus;
import com.yello.server.domain.user.exception.UserPostNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPostRepositoryImpl implements UserPostRepository {

    private final UserPostCommentJpaRepository userPostCommentJpaRepository;
    private final UserPostJpaRepository userPostJpaRepository;

    @Override
    public Long countPostCommentByPostId(Long postId, UserPostCommentStatus status) {
        return userPostCommentJpaRepository.countByPostId(postId, status);
    }

    @Override
    public Long countPostCommentByPostIdAndUserId(Long postId, Long userId, UserPostCommentStatus status) {
        return userPostCommentJpaRepository.countByPostIdAndUserId(postId, userId, status);
    }

    @Override
    public List<UserPostComment> findAllByPostId(Long postId, UserPostCommentStatus status, Pageable pageable) {
        return userPostCommentJpaRepository.findAllByPostId(postId, status, pageable);
    }

    @Override
    public List<UserPostComment> findAllByPostIdAndUserId(Long postId, Long userId, UserPostCommentStatus status,
        Pageable pageable) {
        return userPostCommentJpaRepository.findAllByPostIdAndUserId(postId, userId, status, pageable);
    }

    @Override
    public UserPostComment getCommentById(Long userPostCommentId) {
        return userPostCommentJpaRepository.findById(userPostCommentId)
            .orElseThrow(() -> new UserPostNotFoundException(USER_POST_COMMENT_NOT_FOUND_EXCEPTION));
    }

    @Override
    public UserPostComment save(UserPostComment userPostComment) {
        return userPostCommentJpaRepository.save(userPostComment);
    }

    @Override
    public UserPost getPostById(Long userPostId) {
        return userPostJpaRepository.findById(userPostId)
            .orElseThrow(() -> new UserPostNotFoundException(USER_POST_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Optional<UserPost> findPostById(Long userPostId) {
        return userPostJpaRepository.findById(userPostId);
    }
}
