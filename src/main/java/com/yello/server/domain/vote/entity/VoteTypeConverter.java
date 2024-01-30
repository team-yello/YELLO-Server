package com.yello.server.domain.vote.entity;

import com.yello.server.domain.user.entity.Social;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class VoteTypeConverter implements AttributeConverter<VoteType, String> {

    @Override
    public String convertToDatabaseColumn(VoteType voteType) {
        if (voteType == null) {
            return null;
        }
        return voteType.getIntial();
    }

    @Override
    public VoteType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return VoteType.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
