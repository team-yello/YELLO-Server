package com.yello.server.domain.group.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    indexes = {
        @Index(name = "idx__group__name", columnList = "groupName")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "user_group__sub_group_name__unique",
            columnNames = {"groupName", "subGroupName"}
        ),
    }
)
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String subGroupName;

    @Column(nullable = false)
    @Convert(converter = UserGroupTypeConverter.class)
    @ColumnDefault("\"UNIVERSITY\"")
    private UserGroupType userGroupType;

    public static UserGroup of(String groupName, String subGroupName, UserGroupType userGroupType) {
        return UserGroup.builder()
            .groupName(groupName)
            .subGroupName(subGroupName)
            .userGroupType(userGroupType)
            .build();
    }

    @Override
    public String toString() {
        return switch (this.userGroupType) {
            case UNIVERSITY -> String.format("%s %s", groupName, subGroupName);
            case HIGH_SCHOOL -> String.format("%s %s반", groupName, subGroupName);
            case MIDDLE_SCHOOL -> String.format("%s %s반", groupName, subGroupName);
            case SOPT -> String.format("%s %s", groupName, subGroupName);
        };
    }
}
