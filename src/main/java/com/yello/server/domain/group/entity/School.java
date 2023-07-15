package com.yello.server.domain.group.entity;

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
