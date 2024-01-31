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
        return type.name();
    }

    @Override
    public NoticeType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return NoticeType.fromName(dbData);
    }
}
