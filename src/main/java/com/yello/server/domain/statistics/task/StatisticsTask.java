package com.yello.server.domain.statistics.task;

import com.yello.server.domain.statistics.service.StatisticsService;
import com.yello.server.global.common.util.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatisticsTask {

    private final StatisticsService statisticsService;

    @Scheduled(cron = "0 */30 * * * *", zone = ConstantUtil.GlobalZoneIdLabel)
    public void writeUserGroupStatistics() {
        statisticsService.writeUserGroupStatistics();
    }
}
