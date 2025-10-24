package com.ssafy.alrebaba.common.util;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractCodedEnumConverter<T extends Enum<T> & CodedEnum<E>, E> implements AttributeConverter<T, E> {
    private final Class<T> clazz;

    public AbstractCodedEnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return attribute.getKey();
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return Arrays.stream(clazz.getEnumConstants())
                .filter(e -> e.getKey().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + dbData));
    }
}