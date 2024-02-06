package com.yello.server.domain.admin.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class AdminConfigurationTypeConverter implements AttributeConverter<AdminConfigurationType, String> {

    @Override
    public String convertToDatabaseColumn(AdminConfigurationType type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    @Override
    public AdminConfigurationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return AdminConfigurationType.fromName(dbData);
    }
}
