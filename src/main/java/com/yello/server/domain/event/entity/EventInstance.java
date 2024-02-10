package com.yello.server.domain.event.entity;

import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;

import com.yello.server.global.common.entity.ZonedDateTimeConverter;
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
import java.time.ZonedDateTime;
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
public class EventInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventHistoryId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EventHistory eventHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventTimeId")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private EventTime eventTime;

    @Column(nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime instanceDate;
    
    @Column
    @Builder.Default
    private Long remainEventCount = 0L;

    public static EventInstance of(EventTime eventTime, EventHistory eventHistory) {
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        return EventInstance.builder()
            .eventHistory(eventHistory)
            .eventTime(eventTime)
            .instanceDate(now)
            .build();
    }

    public void subRemainEventCount(Long amount) {
        this.remainEventCount -= amount;
    }
}
