package com.yello.server.domain.user.entity;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserPostStatusConverter implements AttributeConverter<UserPostStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserPostStatus userData) {
        if (userData == null) {
            return null;
        }
        return userData.name();
    }

    @Override
    public UserPostStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UserPostStatus.fromName(dbData);
    }
}
