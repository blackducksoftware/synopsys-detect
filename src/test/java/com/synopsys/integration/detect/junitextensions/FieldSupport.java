package com.synopsys.integration.detect.junitextensions;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;

public class FieldSupport {
    public static Optional<Field> find(Class<?> clazz, String fieldName) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
        }

        return Optional.empty();
    }

    public static Optional<Object> getValue(Object o, String fieldName) {
        Optional<Field> optionalField = find(o.getClass(), fieldName);
        if (optionalField.isPresent()) {
            Field field = optionalField.get();
            field.setAccessible(true);
            try {
                return Optional.of(field.get(o));
            } catch (IllegalAccessException e) {
            }
        }

        return Optional.empty();
    }

    public static <T> T getValueOrDefault(ExtensionContext extensionContext, String fieldName, T defaultValue) {
        return extensionContext
                   .getTestInstance()
                   .flatMap(obj -> getValue(obj, fieldName))
                   .map(fieldValue -> (T) fieldValue)
                   .orElse(defaultValue);
    }

}
