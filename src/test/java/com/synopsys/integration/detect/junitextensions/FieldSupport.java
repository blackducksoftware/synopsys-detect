package com.synopsys.integration.detect.junitextensions;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class FieldSupport {
    public static Optional<Field> find(Class<?> clazz, String fieldName) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
        }

        return Optional.empty();
    }

    public static Optional<Object> getFieldValue(Object o, String fieldName) {
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

    public static <R, F> R useField(Object o, String fieldName, Function<F, R> fieldFunction, F defaultValue) {
            return getFieldValue(o, fieldName)
                .map(fieldValue -> (F)fieldValue)
                .map(fieldFunction)
                .orElse(fieldFunction.apply(defaultValue));
    }

}
