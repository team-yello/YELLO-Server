package com.yello.server.domain.user.entity;

import javax.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDataTypeConverter implements AttributeConverter<UserDataType, String> {

    @Override
    public String convertToDatabaseColumn(UserDataType userData) {
        if (userData == null) {
            return null;
        }
        return userData.name();
    }

    @Override
    public UserDataType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UserDataType.fromName(dbData);
    }
}
