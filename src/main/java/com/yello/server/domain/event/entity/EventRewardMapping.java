package com.yello.server.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class EventRewardMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventTimeId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private EventTime eventTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventRewardId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private EventReward eventReward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventRandomId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private EventRandom eventRandom;

    @Column(columnDefinition = "int NOT NULL CHECK (event_reward_probability BETWEEN 0 and 100)")
    private Integer eventRewardProbability;
}
