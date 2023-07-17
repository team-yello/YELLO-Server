package com.yello.server.domain.cooldown.entity;

import com.yello.server.domain.user.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.yello.server.global.common.util.ConstantUtil.TIMER_TIME;
import static com.yello.server.global.common.util.TimeUtil.getSecondsBetween;

@Getter
@Entity
@Builder
@AllArgsConstructor
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

    public static Cooldown of(User user, LocalDateTime createdAt) {
        return Cooldown.builder()
                .user(user)
                .createdAt(createdAt)
                .build();
    }

    public Boolean isPossible() {
        System.out.println(getSecondsBetween(LocalDateTime.now(), createdAt) + "gsdsdf");
        return getSecondsBetween(LocalDateTime.now(), createdAt) >= TIMER_TIME;
    }
}
