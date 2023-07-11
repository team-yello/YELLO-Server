package com.yello.server.global.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataFormatUtil {

    public static String toDateFormattedString(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dateTimeFormatter).toString();
    }

}
