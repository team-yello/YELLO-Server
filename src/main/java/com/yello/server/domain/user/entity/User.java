package com.yello.server.domain.user.entity;

import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.group.entity.School;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
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
    @ColumnDefault("200")
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

    @Column(nullable = false)
    private Integer groupAdmissionYear;

    @Email
    @Column(nullable = false)
    private String email;

    public static User of(SignUpRequest signUpRequest, String uuid, School group) {
        return User.builder()
                .recommendCount(0L)
                .name(signUpRequest.name())
                .yelloId(signUpRequest.yelloId())
                .gender(signUpRequest.gender())
                .point(200)
                .social(signUpRequest.social())
                .profileImage(signUpRequest.profileImage())
                .uuid(uuid)
                .deletedAt(null)
                .group(group)
                .groupAdmissionYear(signUpRequest.groupAdmissionYear())
                .email(signUpRequest.email())
                .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.point = 0;
    }

    public void renew() {
        this.deletedAt = null;
    }

    public String groupString() {
        return this.group.toString() + " " + this.getGroupAdmissionYear() + "학번";
    }

    public void increaseRecommendCount() {
        this.recommendCount += 1;
    }

    public void plusPoint(Integer point) {
        this.point += point;
    }

    public void minusPoint(Integer point) {
        this.point -= point;
    }

}
