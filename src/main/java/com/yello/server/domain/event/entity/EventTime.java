package com.yello.server.domain.event.entity;

import com.yello.server.global.common.entity.OffsetTimeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.OffsetTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Event event;

    @Column(columnDefinition = "varchar(30) NOT NULL")
    @Convert(converter = OffsetTimeConverter.class)
    private OffsetTime startTime;

    @Column(columnDefinition = "varchar(30) NOT NULL")
    @Convert(converter = OffsetTimeConverter.class)
    private OffsetTime endTime;

    @Column
    @Builder.Default
    private Long rewardCount = 1L;
}
