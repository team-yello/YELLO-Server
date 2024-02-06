package com.yello.server.domain.event.entity;

import com.yello.server.global.common.dto.AuditingTimeEntity;
import com.yello.server.global.common.entity.ZonedDateTimeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.ZonedDateTime;
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
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "tag_unique",
            columnNames = {"tag"}
        )
    }
)
public class Event extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = EventTypeConverter.class)
    private EventType tag;

    @Column(nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime startDate;

    @Column(nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime endDate;

    @Column
    private String title;

    @Column
    private String subTitle;

    /**
     * Lottie를 저장하기 위한 컬럼 [{ ...json1... }, { ...json2... }]
     */
    @Column(columnDefinition = "MEDIUMTEXT")
    private String animation;
}
