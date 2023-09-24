package com.yello.server.domain.group.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class UserGroupTypeConverter implements AttributeConverter<UserGroupType, String> {

    @Override
    public String convertToDatabaseColumn(UserGroupType userGroupType) {
        if (userGroupType == null) {
            return null;
        }
        return userGroupType.getIntial();
    }

    @Override
    public UserGroupType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return UserGroupType.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
