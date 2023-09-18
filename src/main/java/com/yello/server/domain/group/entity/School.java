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
        @Index(name = "idx__school_name", columnList = "schoolName")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "school_department_name_unique",
            columnNames = {"schoolName", "departmentName"}
        ),
    }
)
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private String departmentName;

    @Column(nullable = false)
    @Convert(converter = SchoolTypeConverter.class)
    @ColumnDefault("UNIVERSITY")
    private SchoolType schoolType;

    public static School of(String schoolName, String departmentName) {
        return School.builder()
            .schoolName(schoolName)
            .departmentName(departmentName)
            .build();
    }

    @Override
    public String toString() {
        return String.format("%s %s", schoolName, departmentName);
    }
}
