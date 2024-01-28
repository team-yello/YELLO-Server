package com.yello.server.domain.admin.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class AdminConfigurationTypeConverter implements AttributeConverter<AdminConfigurationType, String> {

    @Override
    public String convertToDatabaseColumn(AdminConfigurationType type) {
        if (type == null) {
            return null;
        }
        return type.getIntial();
    }

    @Override
    public AdminConfigurationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return AdminConfigurationType.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
