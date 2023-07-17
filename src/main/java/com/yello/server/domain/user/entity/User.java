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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
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

    @Column(nullable = false)
    private Integer groupAdmissionYear;

    @Email
    @Column(nullable = false)
    private String email;

    public User(Long recommendCount, String name, String yelloId, Gender gender, Integer point, Social social,
        String profileImage, String uuid, LocalDateTime deletedAt, School group, Integer groupAdmissionYear,
        String email) {
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
        this.groupAdmissionYear = groupAdmissionYear;
        this.email = email;
    }

    public static User of(SignUpRequest signUpRequest, String uuid, School group) {
        return User.builder()
            .recommendCount(0L)
            .name(signUpRequest.name())
            .yelloId(signUpRequest.yelloId())
            .gender(signUpRequest.gender())
            .point(0)
            .social(signUpRequest.social())
            .profileImage(signUpRequest.profileImage())
            .uuid(uuid)
            .deletedAt(null)
            .group(group)
            .groupAdmissionYear(signUpRequest.groupAdmissionYear())
            .email(signUpRequest.email())
            .build();
    }

    public void addRecommendCount(int count) {
        this.recommendCount += count;
    }

    public void plusPoint(Integer point) {
        this.point += point;
    }

    public void minusPoint(Integer point) {
        this.point -= point;
    }

}
