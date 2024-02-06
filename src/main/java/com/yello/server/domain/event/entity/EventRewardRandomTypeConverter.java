package com.yello.server.domain.event.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class EventRewardRandomTypeConverter implements AttributeConverter<EventRewardRandomType, String> {

    @Override
    public String convertToDatabaseColumn(EventRewardRandomType type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    @Override
    public EventRewardRandomType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return EventRewardRandomType.fromName(dbData);
    }
}

