package com.yello.server.domain.event.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class EventRewardTypeConverter implements AttributeConverter<EventRewardType, String> {

    @Override
    public String convertToDatabaseColumn(EventRewardType type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    @Override
    public EventRewardType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return EventRewardType.fromName(dbData);
    }
}

