package com.yello.server.domain.statistics.task;

import com.yello.server.domain.statistics.service.StatisticsService;
import com.yello.server.global.common.util.ConstantUtil;
import com.yello.server.infrastructure.slack.factory.SlackWebhookMessageFactory;
import com.yello.server.infrastructure.slack.service.SlackService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatisticsTask {

    private final SlackWebhookMessageFactory messageFactory;
    private final SlackService slackService;
    private final StatisticsService statisticsService;

    @Scheduled(cron = "0 */30 * * * *", zone = ConstantUtil.GlobalZoneIdLabel)
    public void writeUserGroupStatistics() {
        statisticsService.writeUserGroupStatistics();
    }

    @Scheduled(cron = "*/10 * * * * *", zone = ConstantUtil.GlobalZoneIdLabel)
    public void calculateDailyStatistics() throws IOException {
//        final StatisticsDaily statisticsDaily = statisticsService.writeDailyServiceStatistics();
//        slackService.send(SlackChannel.DATA_DAILY, messageFactory.generateDataDailyPayload(statisticsDaily));
    }
}
