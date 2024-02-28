package com.yello.server.domain.vote.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class VoteTypeConverter implements AttributeConverter<VoteType, String> {

    @Override
    public String convertToDatabaseColumn(VoteType voteType) {
        if (voteType == null) {
            return null;
        }
        return voteType.name();
    }

    @Override
    public VoteType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return VoteType.fromName(dbData);
    }
}
