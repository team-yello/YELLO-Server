package com.yello.server.global.common.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class JsonConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(T data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public T convertToEntityAttribute(String database) {
        if (database == null) {
            return null;
        }

        try {
            return objectMapper.readValue(database, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
