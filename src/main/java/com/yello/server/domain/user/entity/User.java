package com.yello.server.domain.user.entity;

import com.yello.server.domain.admin.dto.request.AdminUserDetailRequest;
import com.yello.server.domain.authorization.dto.request.SignUpRequest;
import com.yello.server.domain.group.entity.UserGroup;
import com.yello.server.global.common.dto.AuditingTimeEntity;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "yelloId_unique",
            columnNames = {"yello_id"}
        ),
        @UniqueConstraint(
            name = "uuid_unique",
            columnNames = {"uuid"}
        ),
        @UniqueConstraint(
            name = "deviceToken_unique",
            columnNames = {"device_token"}
        )
    }
)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long recommendCount;

    @Column(nullable = false)
    private String name;

    @Column(name = "yello_id", nullable = false)
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

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    private UserGroup group;

    @Column(nullable = false)
    private int groupAdmissionYear;

    @Email
    @Column(nullable = false)
    private String email;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer ticketCount;

    @Getter
    @Column(name = "device_token")
    private String deviceToken;

    @Column(nullable = false)
    @ColumnDefault("normal")
    @Convert(converter = SubscribeConverter.class)
    private Subscribe subscribe;

    public static User of(SignUpRequest signUpRequest, UserGroup group) {
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
            .deviceToken(Objects.equals(signUpRequest.deviceToken(), "") ? null
                : signUpRequest.deviceToken())
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

    public void update(AdminUserDetailRequest request) {
        this.recommendCount = request.recommendCount();
        this.name = request.name();
        this.yelloId = request.yelloId();
        this.gender = request.gender();
        this.point = request.point();
        this.social = request.social();
        this.profileImage = request.profileImage();
        this.uuid = request.uuid();
        this.groupAdmissionYear = request.groupAdmissionYear();
        this.email = request.email();
        this.ticketCount = request.ticketCount();
        this.deviceToken = request.deviceToken();
        this.subscribe = request.subscribe();
    }

    public void addPoint(Integer point) {
        if (this.getSubscribe() == Subscribe.NORMAL) {
            this.point += point;
            return;
        }
        this.point += point * 2;
    }

    public void subPoint(Integer point) {
        this.point -= point;
    }

    public void addRecommendCount(Long recommendCount) {
        this.recommendCount += recommendCount;
    }

    public void addTicketCount(int ticketCount) {
        this.ticketCount += ticketCount;
    }

    public void subTicketCount(int ticketCount) {
        if (this.ticketCount - ticketCount <= 0) {
            this.ticketCount = 0;
        } else {
            this.ticketCount -= ticketCount;
        }
    }


    public String toGroupString() {
        /**
         * TODO 고등학교 중학교 처참함. groupAdmissionYear를 '입학년도'로 볼 것이 아닌,
         * TODO UserGroup과 종속된 User 하나에게 주어지는 '고유한 값'(1:1 특성)으로 보아야함.
         * TODO '학번', '번호', 등은 해당 유저의 각각 대학교, 고등학교이라는 그룹 내에서 가지는 유저만의 (일부 고유 식별 가능한) 특성임.
         * */

        return switch (this.group.getUserGroupType()) {
            case UNIVERSITY -> String.format("%s %s학번", this.group.toString(), this.groupAdmissionYear);
            case HIGH_SCHOOL -> String.format("%s %s학년 %s반", this.group.getGroupName(), this.groupAdmissionYear,
                this.group.getSubGroupName());
            case MIDDLE_SCHOOL -> String.format("%s %s학년 %s반", this.group.getGroupName(), this.groupAdmissionYear,
                this.group.getSubGroupName());
            case SOPT -> String.format("%s", this.group.toString());
        };
    }

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }

    public void setDeviceToken(String deviceToken) {
        if (Objects.equals(deviceToken, "")) {
            this.deviceToken = null;
        } else {
            this.deviceToken = deviceToken;
        }
    }
}
