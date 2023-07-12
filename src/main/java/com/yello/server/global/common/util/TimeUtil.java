package com.yello.server.global.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final int SECOND = 60;
    private static final int MINUTE = 60;
    private static final int HOUR = 24;

    public static String toFormattedString(LocalDateTime localDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, currentDateTime);
        long seconds = duration.getSeconds();

        if (seconds < SECOND) {
            return seconds + "초 전";
        } else if ((seconds /= SECOND) < MINUTE) {
            return seconds + "분 전";
        } else if ((seconds /= MINUTE) < HOUR) {
            return (seconds) + "시간 전";
        }

        return (seconds) + "일 전";
    }

    public static long timeDiff(LocalDateTime localDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(localDateTime, currentDateTime);
        return duration.getSeconds();
    }

    public static String toDateFormattedString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dateTimeFormatter).toString();
    }
}
