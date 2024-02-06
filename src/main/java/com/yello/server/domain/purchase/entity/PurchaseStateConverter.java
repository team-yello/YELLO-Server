package com.yello.server.domain.purchase.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class PurchaseStateConverter implements AttributeConverter<PurchaseState, String> {

    @Override
    public String convertToDatabaseColumn(PurchaseState purchaseState) {
        if (purchaseState == null) {
            return null;
        }
        return purchaseState.name();
    }

    @Override
    public PurchaseState convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return PurchaseState.fromName(dbData);
    }
}
