package com.yello.server.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventRandom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String randomTag;

    /**
     * x의 정의역 [0,1] y의 정의역 [minValue, maxValue] 예시) RANDOM [{ x: 0.5, y: 80 }, { x : 1, y: 20}] FIXED [{ x: 1, y: 100}]
     */
    @Column
    private String probabilityPointList;
}
