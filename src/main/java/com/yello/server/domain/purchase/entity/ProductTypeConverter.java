package com.yello.server.domain.purchase.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Converter
@Log4j2
public class ProductTypeConverter implements AttributeConverter<ProductType, String> {

    @Override
    public String convertToDatabaseColumn(ProductType productType) {
        if (productType == null) {
            return null;
        }
        return productType.name();
    }

    @Override
    public ProductType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return ProductType.fromName(dbData);
    }
}
