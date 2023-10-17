package com.yello.server.domain.purchase.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class PurchaseStateConverter implements AttributeConverter<PurchaseState, String> {

    @Override
    public String convertToDatabaseColumn(PurchaseState purchaseState) {
        if (purchaseState == null) {
            return null;
        }
        return purchaseState.getIntial();
    }

    @Override
    public PurchaseState convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return PurchaseState.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }

}
