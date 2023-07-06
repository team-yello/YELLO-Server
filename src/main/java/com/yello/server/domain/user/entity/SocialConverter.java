package com.yello.server.domain.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SocialConverter implements AttributeConverter<Social, String> {

    @Override
    public String convertToDatabaseColumn(Social social) {
        if(social == null) return null;
        return social.getIntial();
    }

    @Override
    public Social convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        try {
            return Social.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            System.out.println("failure to convert cause unexpected code" + dbData + e);
            throw e;
        }
    }
}
