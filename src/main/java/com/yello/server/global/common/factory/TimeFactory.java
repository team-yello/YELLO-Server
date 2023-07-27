package com.yello.server.global.common.factory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFactory {

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

        return (seconds / HOUR) + "일 전";
    }

    public static long getSecondsBetween(LocalDateTime currentDateTime, LocalDateTime localDateTime) {
        Duration duration = Duration.between(localDateTime, currentDateTime);
        return duration.getSeconds();
    }

    public static String toDateFormattedString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dateTimeFormatter).toString();
    }

    public static LocalDateTime plusTime(LocalDateTime localDateTime, int time) {
        return localDateTime.plusMinutes(time);
    }

    public static LocalDateTime minusTime(LocalDateTime localDateTime, int time) {
        return localDateTime.minusMinutes(time);
    }
}
