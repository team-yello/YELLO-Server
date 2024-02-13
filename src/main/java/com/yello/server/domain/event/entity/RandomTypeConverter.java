package com.yello.server.domain.event.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class RandomTypeConverter implements AttributeConverter<RandomType, String> {

    @Override
    public String convertToDatabaseColumn(RandomType type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    @Override
    public RandomType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return RandomType.fromName(dbData);
    }
}
