package com.yello.server.domain.user.repository;

import com.yello.server.domain.user.entity.UserPostComment;
import com.yello.server.domain.user.entity.UserPostCommentStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserPostCommentJpaRepository extends JpaRepository<UserPostComment, Long> {

    @Query("SELECT count(upc) FROM UserPostComment upc WHERE upc.userPost.id = ?1 AND upc.status = ?2")
    Long countByPostId(Long postId, UserPostCommentStatus status);

    @Query("SELECT count(upc) FROM UserPostComment upc WHERE upc.userPost.id = ?1 AND upc.user.id = ?2 AND upc.status = ?3")
    Long countByPostIdAndUserId(Long postId, Long userId, UserPostCommentStatus status);

    @Query("SELECT upc FROM UserPostComment upc WHERE upc.userPost.id = ?1 AND upc.status = ?2")
    List<UserPostComment> findAllByPostId(Long postId, UserPostCommentStatus status, Pageable pageable);

    @Query("SELECT upc FROM UserPostComment upc WHERE upc.userPost.id = ?1 AND upc.user.id = ?2 AND upc.status = ?3")
    List<UserPostComment> findAllByPostIdAndUserId(Long postId, Long userId, UserPostCommentStatus status,
        Pageable pageable);
}
