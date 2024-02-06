package com.yello.server.domain.group.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class UserGroupDataTagConterver implements AttributeConverter<UserGroupDataTag, String> {

    @Override
    public String convertToDatabaseColumn(UserGroupDataTag userGroupType) {
        if (userGroupType == null) {
            return null;
        }
        return userGroupType.name();
    }

    @Override
    public UserGroupDataTag convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UserGroupDataTag.fromName(dbData);
    }
}
