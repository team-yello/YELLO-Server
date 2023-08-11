package com.yello.server.domain.purchase.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class ProductTypeConverter implements AttributeConverter<ProductType, String> {

    @Override
    public String convertToDatabaseColumn(ProductType productType) {
        if (productType == null) {
            return null;
        }
        return productType.getIntial();
    }

    @Override
    public ProductType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return ProductType.fromCode(dbData);
        } catch (IllegalArgumentException exception) {
            log.error("failure to convert cause unexpected code" + dbData + exception);
            throw exception;
        }
    }
}
