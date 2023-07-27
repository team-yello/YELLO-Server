package com.yello.server.domain.group.entity;

import lombok.*;

import javax.persistence.*;

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
