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
        return userGroupType.name();
    }

    @Override
    public UserGroupType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UserGroupType.fromName(dbData);
    }
}
