package com.yello.server.domain.pay.entity;

import com.yello.server.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer optionIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public static Pay of(Integer optionIndex, User user) {
        return Pay.builder()
                .optionIndex(optionIndex)
                .user(user)
                .build();
    }

    public static Pay createPay(Integer optionIndex, User user) {
        return Pay.of(optionIndex, user);
    }
}
