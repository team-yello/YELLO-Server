package com.yello.server.domain.event.entity;

import com.yello.server.global.common.dto.AuditingTimeEntity;
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
public class EventInstanceReward extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventInstanceId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EventInstance eventInstance;

    @Column(nullable = false)
    private String rewardTag;

    @Column(nullable = false)
    @Builder.Default
    private Long rewardValue = 0L;

    @Column
    private String rewardTitle;

    @Column
    private String rewardImage;
    public static EventInstanceReward of(EventInstance eventInstance, EventReward eventReward) {
        return EventInstanceReward.builder()
            .eventInstance(eventInstance)
            .rewardTag(eventReward.getTag())
            .rewardValue(eventReward.getMinRewardValue())
            .rewardTitle(String.format(eventReward.getRewardTitle(), eventReward.getMinRewardValue()))
            .rewardImage(eventReward.getRewardImage())
            .build();
    }

}
