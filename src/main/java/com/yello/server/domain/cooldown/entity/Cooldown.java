package com.yello.server.domain.cooldown.entity;

import static com.yello.server.global.common.factory.TimeFactory.getSecondsBetween;
import static com.yello.server.global.common.util.ConstantUtil.TIMER_TIME;

import com.yello.server.domain.user.entity.User;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "userId_unique",
            columnNames = {"userId"}
        ),
    }
)
public class Cooldown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    private String messageId;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    public static Cooldown of(User user, String messageId, LocalDateTime createdAt) {
        return Cooldown.builder()
            .user(user)
            .messageId(messageId)
            .createdAt(createdAt)
            .build();
    }

    public Boolean isPossible() {
        return getSecondsBetween(LocalDateTime.now(), createdAt) >= TIMER_TIME;
    }

    public void updateDate(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void renew() {
        this.deletedAt = null;
    }
}
