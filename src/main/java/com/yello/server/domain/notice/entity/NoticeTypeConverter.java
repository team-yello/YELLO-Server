package com.yello.server.domain.notice.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class NoticeTypeConverter implements AttributeConverter<NoticeType, String> {

    @Override
    public String convertToDatabaseColumn(NoticeType type) {
        if (type == null) {
            return null;
        }
        return type.getIntial();
    }

    @Override
    public NoticeType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return NoticeType.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
