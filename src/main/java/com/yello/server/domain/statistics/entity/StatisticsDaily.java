package com.yello.server.domain.statistics.entity;

import com.yello.server.domain.statistics.dto.RevenueVO;
import com.yello.server.domain.statistics.dto.SignUpVO;
import com.yello.server.domain.statistics.dto.VoteVO;
import com.yello.server.global.common.entity.JsonConverter;
import com.yello.server.global.common.entity.ZonedDateTimeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime startAt;

    @Column(nullable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime endAt;

    @Column(length = 2047)
    @Convert(converter = JsonConverter.class)
    private SignUpVO signUp;

    @Column(length = 2047)
    @Convert(converter = JsonConverter.class)
    private RevenueVO revenue;

    @Column(length = 2047)
    @Convert(converter = JsonConverter.class)
    private VoteVO vote;
}
