package com.yello.server.domain.user.entity;

import static com.yello.server.global.common.factory.TimeFactory.getSecondsBetween;
import static com.yello.server.global.common.util.ConstantUtil.ADMOB_TIMER_TIME;

import com.yello.server.global.common.factory.TimeFactory;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
@Table
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    @Convert(converter = UserDataTypeConverter.class)
    private UserDataType tag;

    @Column(nullable = false)
    private String value;

    public static UserData of(UserDataType tag, String value, User user) {
        return UserData.builder()
            .tag(tag)
            .user(user)
            .value(value)
            .build();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isPossible(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return TimeFactory.getSecondsBetween(LocalDateTime.now(), LocalDateTime.parse(this.value, formatter)) >= ADMOB_TIMER_TIME;
    }
}
