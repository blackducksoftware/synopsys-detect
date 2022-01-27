package com.synopsys.integration.detect.configuration.properties;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.enums.EnumProperty;
import com.synopsys.integration.configuration.property.types.integer.IntegerProperty;
import com.synopsys.integration.detect.configuration.validation.DeprecatedValue;

public class EnumDetectProperty<E extends Enum<E>> extends DetectProperty<EnumProperty<E>> {
    public EnumDetectProperty(@NotNull String key, @NotNull E defaultValue, @NotNull Class<E> enumClass) {
        super(new EnumProperty<>(key, defaultValue, enumClass));
    }

    public static <E extends Enum<E>> DetectPropertyBuilder<EnumProperty<E>, EnumDetectProperty<E>> newBuilder(@NotNull String key, @NotNull E defaultValue, @NotNull Class<E> enumClass) {
        DetectPropertyBuilder<EnumProperty<E>, EnumDetectProperty<E>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new EnumDetectProperty<E>(key, defaultValue, enumClass));
        return builder;
    }

    public EnumDetectProperty<E> deprecateValue(E value, String reason) {
        getProperty().deprecateValue(value, reason);
        return this;
    }
}
