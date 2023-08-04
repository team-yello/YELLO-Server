package com.yello.server.domain.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class SocialConverter implements AttributeConverter<Social, String> {

    @Override
    public String convertToDatabaseColumn(Social social) {
        if (social == null) {
            return null;
        }
        return social.getIntial();
    }

    @Override
    public Social convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return Social.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
