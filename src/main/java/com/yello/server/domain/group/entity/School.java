package com.yello.server.domain.group.entity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(indexes = {
        @Index(name = "idx__school_name", columnList = "schoolName")
})
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private String departmentName;

    @Builder
    public School(String schoolName, String departmentName) {
        this.schoolName = schoolName;
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return String.format("%s %s", schoolName, departmentName);
    }
}
