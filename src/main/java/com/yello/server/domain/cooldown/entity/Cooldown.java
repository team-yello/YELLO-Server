package com.yello.server.domain.cooldown.entity;

import com.yello.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public Cooldown(User user, LocalDateTime createdAt) {
        this.user = user;
        this.createdAt = createdAt;
    }
}
