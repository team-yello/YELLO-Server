package com.yello.server.domain.group.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class SchoolTypeConverter implements AttributeConverter<SchoolType, String> {

    @Override
    public String convertToDatabaseColumn(SchoolType schoolType) {
        if (schoolType==null) {
            return null;
        }
        return schoolType.getIntial();
    }

    @Override
    public SchoolType convertToEntityAttribute(String dbData) {
        if (dbData==null) {
            return null;
        }
        try {
            return SchoolType.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
