package com.synopsys.integration.detect.junitextensions;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class FieldSupport {
    public static Optional<Field> find(Class<?> clazz, String fieldName) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    public static <R, F> R useField(Object o, String fieldName, Function<F, R> fieldFunction, F defaultValue) {
        Optional<Field> optionalField = find(o.getClass(), fieldName);
        if (optionalField.isPresent()) {
            Field field = optionalField.get();
            try {
                field.setAccessible(true);
                F fieldValue = (F)field.get(o);
                return fieldFunction.apply(fieldValue);
            } catch (IllegalAccessException e) {
            }
        }

        return fieldFunction.apply(defaultValue);
    }

}
