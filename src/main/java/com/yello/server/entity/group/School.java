package com.yello.server.entity.group;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(nullable = false)
    private Long admissionYear;

    @Builder
    public School(String schoolName, String departmentName, Long admissionYear) {
        this.schoolName = schoolName;
        this.departmentName = departmentName;
        this.admissionYear = admissionYear;
    }
}
