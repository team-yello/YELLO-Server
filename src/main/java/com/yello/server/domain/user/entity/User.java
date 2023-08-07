package com.yello.server.domain.user.entity;

import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.group.entity.School;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer ticketCount;

    @Column(nullable = false)
    private String deviceToken;

    @Column(nullable = false)
    @ColumnDefault("normal")
    @Convert(converter = SubscribeConverter.class)
    private Subscribe subscribe;

    public static User of(SignUpRequest signUpRequest, School group) {
        return User.builder()
            .recommendCount(0L)
            .name(signUpRequest.name())
            .yelloId(signUpRequest.yelloId())
            .gender(signUpRequest.gender())
            .point(200)
            .social(signUpRequest.social())
            .profileImage(signUpRequest.profileImage())
            .uuid(signUpRequest.uuid())
            .deletedAt(null)
            .group(group)
            .groupAdmissionYear(signUpRequest.groupAdmissionYear())
            .email(signUpRequest.email())
            .deviceToken(signUpRequest.deviceToken())
            .subscribe(Subscribe.NORMAL)
            .ticketCount(0)
            .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.point = 0;
        this.deviceToken = null;
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

    public String getDeviceToken() {
        return this.deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

}
