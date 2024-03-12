package com.yello.server.domain.user.entity;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserPostCommentStatusConverter implements AttributeConverter<UserPostCommentStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserPostCommentStatus userData) {
        if (userData == null) {
            return null;
        }
        return userData.name();
    }

    @Override
    public UserPostCommentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return UserPostCommentStatus.fromName(dbData);
    }
}
