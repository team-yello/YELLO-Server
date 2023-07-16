package com.yello.server.domain.pay.entity;

import com.yello.server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer optionIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public Pay(Integer optionIndex, User user) {
        this.optionIndex = optionIndex;
        this.user = user;
    }

    public static Pay createPay(Integer optionIndex, User user) {
        return new Pay(optionIndex, user);
    }
}
