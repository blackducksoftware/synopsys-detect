package com.synopsys.integration.detect.configuration.properties;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.configuration.property.types.enums.EnumProperty;

public class ExtendedEnumDetectProperty<E extends Enum<E>, B extends Enum<B>> extends DetectProperty<ExtendedEnumProperty<E, B>> {
    public ExtendedEnumDetectProperty(@NotNull String key, @NotNull ExtendedEnumValue<E, B> defaultValue, @NotNull Class<E> eClass, @NotNull Class<B> bClass) {
        super(new ExtendedEnumProperty<>(key, defaultValue, eClass, bClass));
    }

    public static <E extends Enum<E>, B extends Enum<B>> DetectPropertyBuilder<ExtendedEnumProperty<E, B>, ExtendedEnumDetectProperty<E, B>> newBuilder(@NotNull String key, @NotNull ExtendedEnumValue<E, B> defaultValue,
        @NotNull Class<E> eClass,
        @NotNull Class<B> bClass) {
        DetectPropertyBuilder<ExtendedEnumProperty<E, B>, ExtendedEnumDetectProperty<E, B>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new ExtendedEnumDetectProperty<E, B>(key, defaultValue, eClass, bClass));
        return builder;
    }
}
