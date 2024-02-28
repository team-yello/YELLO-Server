package com.yello.server.global.common.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Converter
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, String> {

    @Override
    public String convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }

        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }

        return ZonedDateTime.parse(sqlTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
