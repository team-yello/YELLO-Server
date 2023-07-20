package com.yello.server.domain.cooldown.entity;

import static com.yello.server.global.common.util.ConstantUtil.TIMER_TIME;
import static com.yello.server.global.common.util.TimeUtil.getSecondsBetween;

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
public class Cooldown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    public static Cooldown of(User user, LocalDateTime createdAt) {
        return Cooldown.builder()
            .user(user)
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
