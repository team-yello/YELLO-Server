package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.UserPost;
import com.yello.server.domain.user.entity.UserPostComment;
import com.yello.server.domain.user.entity.UserPostCommentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface UserPostRepository {

    Long countPostCommentByPostId(Long postId, UserPostCommentStatus status);

    Long countPostCommentByPostIdAndUserId(Long postId, Long userId, UserPostCommentStatus status);

    List<UserPostComment> findAllByPostId(Long postId, UserPostCommentStatus status, Pageable pageable);

    List<UserPostComment> findAllByPostIdAndUserId(Long postId, Long userId, UserPostCommentStatus status,
        Pageable pageable);

    UserPostComment getCommentById(Long userPostCommentId);

    UserPostComment save(UserPostComment userPostComment);

    UserPost getPostById(Long userPostId);

    Optional<UserPost> findPostById(Long userPostId);

}
