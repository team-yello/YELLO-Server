package com.yello.server.domain.purchase.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class GatewayConverter implements AttributeConverter<Gateway, String> {

    @Override
    public String convertToDatabaseColumn(Gateway gateway) {
        if (gateway == null) {
            return null;
        }
        return gateway.name();
    }

    @Override
    public Gateway convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Gateway.fromName(dbData);
    }
}
