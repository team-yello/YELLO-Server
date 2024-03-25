package com.yello.server.domain.statistics.repository;

import com.yello.server.domain.statistics.entity.StatisticsDaily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsDailyJpaRepository extends JpaRepository<StatisticsDaily, Long> {

}
