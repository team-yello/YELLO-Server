package com.yello.server.domain.user.entity;

import com.yello.server.domain.user.dto.request.UserPostCommentUpdateRequest;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPostComment extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "userPostId", nullable = false)
    private UserPost userPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "userId", nullable = true)
    private User user;

    @Column(nullable = false)
    @Convert(converter = UserPostCommentStatusConverter.class)
    private UserPostCommentStatus status;

    @Column
    private String userName;

    @Column
    private String yelloId;

    @Column
    private String title;

    @Column
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void update(UserPostCommentUpdateRequest request, UserPost userPost, User user,
        UserPostCommentStatus status) {
        this.userPost = userPost;
        this.user = user;
        this.status = status;
        this.userName = request.userName();
        this.yelloId = request.yelloId();
        this.title = request.title();
        this.subtitle = request.subtitle();
        this.content = request.content();
    }
}
