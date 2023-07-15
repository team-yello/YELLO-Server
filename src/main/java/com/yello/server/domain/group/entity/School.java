package com.yello.server.domain.group.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
