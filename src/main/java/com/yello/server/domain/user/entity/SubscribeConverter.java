package com.yello.server.domain.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class SubscribeConverter implements AttributeConverter<Subscribe, String> {

    @Override
    public String convertToDatabaseColumn(Subscribe subscribe) {
        if (subscribe == null) {
            return null;
        }
        return subscribe.name();
    }

    @Override
    public Subscribe convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Subscribe.fromName(dbData);
    }
}
