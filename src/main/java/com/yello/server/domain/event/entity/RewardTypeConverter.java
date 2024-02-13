package com.yello.server.domain.event.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class RewardTypeConverter implements AttributeConverter<RewardType, String> {

    @Override
    public String convertToDatabaseColumn(RewardType type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    @Override
    public RewardType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return RewardType.fromName(dbData);
    }
}
