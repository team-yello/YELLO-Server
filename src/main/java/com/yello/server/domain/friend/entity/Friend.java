package com.yello.server.domain.friend.entity;

import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "friend_relationship_unique",
            columnNames = {"user", "target"}
        ),
    }
)
public class Friend extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "target")
    private User target;

    @Column
    private LocalDateTime deletedAt;

    public static Friend of(User user, User target) {
        return Friend.builder()
            .user(user)
            .target(target)
            .deletedAt(null)
            .build();
    }

    public static Friend createFriend(User user, User target) {
        return Friend.of(user, target);
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void renew() {
        this.deletedAt = null;
    }
}
