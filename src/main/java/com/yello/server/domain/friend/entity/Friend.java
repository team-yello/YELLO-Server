package com.yello.server.domain.friend.entity;

import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends AuditingTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followerId")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followingId")
    private User following;

    @Builder
    public Friend(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    public static Friend newFriend(User follower, User following) {
        return new Friend(follower, following);
    }
}
