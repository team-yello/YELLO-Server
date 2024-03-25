package com.yello.server.domain.statistics.task;

import com.yello.server.domain.statistics.entity.StatisticsDaily;
import com.yello.server.domain.statistics.service.StatisticsService;
import com.yello.server.global.common.util.ConstantUtil;
import com.yello.server.infrastructure.slack.dto.response.SlackChannel;
import com.yello.server.infrastructure.slack.factory.SlackWebhookMessageFactory;
import com.yello.server.infrastructure.slack.service.SlackService;
import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
@RequiredArgsConstructor
@RestController
public class StatisticsTask {

    private final SlackWebhookMessageFactory messageFactory;
    private final SlackService slackService;
    private final StatisticsService statisticsService;

    @Scheduled(cron = "0 */30 * * * *", zone = ConstantUtil.GlobalZoneIdLabel)
    public void writeUserGroupStatistics() {
        statisticsService.writeUserGroupStatistics();
    }

    @Scheduled(cron = "1 0 0 * * *", zone = ConstantUtil.GlobalZoneIdLabel)
    public void calculateDailyStatistics() throws IOException {
        final ZonedDateTime now = ZonedDateTime.now(ConstantUtil.GlobalZoneId);
        final StatisticsDaily statisticsDaily = statisticsService.writeDailyServiceStatistics(
            now.minusDays(1).toLocalDate(), now.toLocalDate());

        slackService.send(SlackChannel.DATA_DAILY, messageFactory.generateDataDailyPayload(statisticsDaily));
    }

//    @GetMapping("/statistics")
//    public void kk(@RequestParam("date") String date) throws IOException {
//        LocalDate now = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
//        final StatisticsDaily statisticsDaily = statisticsService.writeDailyServiceStatistics(
//            now.minusDays(1), now);
//
//        slackService.send(SlackChannel.DATA_DAILY, messageFactory.generateDataDailyPayload(statisticsDaily));
//    }
}
