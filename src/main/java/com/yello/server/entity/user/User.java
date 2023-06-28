package com.yello.server.entity.user;

import com.yello.server.entity.AuditingTimeEntity;
import com.yello.server.entity.group.School;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long recommendCount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String yelloId;

    @Column(nullable = false)
    private String sex;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long point;

    @Column(nullable = false)
    private String social;

    @Column
    private String profileImage;

    @Column(nullable = false)
    private long uuid;

    @Column(nullable = false)
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    private School group;


    @Builder
    public User(Long recommendCount, String name, String yelloId, String sex, long point, String social, String profileImage, long uuid, LocalDateTime deletedAt, School group) {
        this.recommendCount = recommendCount;
        this.name = name;
        this.yelloId = yelloId;
        this.sex = sex;
        this.point = point;
        this.social = social;
        this.profileImage = profileImage;
        this.uuid = uuid;
        this.deletedAt = deletedAt;
        this.group = group;
    }
}
