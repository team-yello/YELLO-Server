package com.yello.server.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
public class EventReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tag;

    @Column
    @Builder.Default
    private Long maxRewardValue = 0L;

    @Column
    @Builder.Default
    private Long minRewardValue = 0L;

    @Column
    private String title;

    @Column
    private String image;

    @Column
    private String rewardTitle;

    @Column
    private String rewardImage;
}
