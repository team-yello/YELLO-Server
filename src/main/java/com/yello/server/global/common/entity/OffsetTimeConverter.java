package com.yello.server.global.common.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

@Converter
public class OffsetTimeConverter implements AttributeConverter<OffsetTime, String> {

    @Override
    public String convertToDatabaseColumn(OffsetTime offsetTime) {
        if (offsetTime == null) {
            return null;
        }

        return offsetTime.format(DateTimeFormatter.ISO_OFFSET_TIME);
    }

    @Override
    public OffsetTime convertToEntityAttribute(String sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }

        return OffsetTime.parse(sqlTimestamp, DateTimeFormatter.ISO_OFFSET_TIME);
    }
}
