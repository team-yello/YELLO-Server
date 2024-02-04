package com.yello.server.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
public class EventInstanceReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventInstanceId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EventInstance eventInstance;

    @Column(columnDefinition = "int NOT NULL CHECK (event_reward_probability BETWEEN 0 and 100)")
    private Integer eventRewardProbability;

    @Column(nullable = false)
    @Convert(converter = EventRewardTypeConverter.class)
    private EventRewardType rewardTag;

    @Column
    private String rewardTitle;

    @Column
    private String rewardImage;

    @Column(nullable = false)
    @Convert(converter = EventRewardRandomTypeConverter.class)
    private EventRewardRandomType randomTag;

    @Column(nullable = false)
    private Long maxRewardValue;

    @Column(nullable = false)
    private Long minRewardValue;

    @Column(nullable = false)
    private Long sumRewardValue;
}
