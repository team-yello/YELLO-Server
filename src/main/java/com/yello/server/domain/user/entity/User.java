package com.yello.server.domain.user.entity;

import com.yello.server.domain.group.entity.School;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long recommendCount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String yelloId;

    @Column(nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer point;

    @Column(nullable = false)
    @Convert(converter = SocialConverter.class)
    private Social social;

    @Column
    private String profileImage;

    @Column(nullable = false)
    private String uuid;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    private School group;


    @Builder
    public User(Long recommendCount, String name, String yelloId, Gender gender, Integer point, Social social,
        String profileImage, String uuid, LocalDateTime deletedAt, School group) {
        this.recommendCount = recommendCount;
        this.name = name;
        this.yelloId = yelloId;
        this.gender = gender;
        this.point = point;
        this.social = social;
        this.profileImage = profileImage;
        this.uuid = uuid;
        this.deletedAt = deletedAt;
        this.group = group;
    }
}
