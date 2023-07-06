package com.yello.server.domain.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if(gender == null) return null;
        return gender.getIntial();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        try {
            return Gender.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            System.out.println("failure to convert cause unexpected code" + dbData + e);
            throw e;
        }
    }
}
