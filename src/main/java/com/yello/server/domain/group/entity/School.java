package com.yello.server.domain.group.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(indexes = {
    @Index(name = "idx__school_name", columnList = "schoolName")
})
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private String departmentName;

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
