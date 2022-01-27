package com.synopsys.integration.detect.configuration.properties;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.enums.EnumListProperty;
import com.synopsys.integration.configuration.property.types.string.StringListProperty;

public class EnumListDetectProperty<E extends Enum<E>> extends DetectProperty<EnumListProperty<E>> {
    public EnumListDetectProperty(@NotNull String key, @NotNull List<E> defaultValue, @NotNull Class<E> enumClass) {
        super(new EnumListProperty<>(key, defaultValue, enumClass));
    }

    public static <E extends Enum<E>> DetectPropertyBuilder<EnumListProperty<E>, EnumListDetectProperty<E>> newBuilder(@NotNull String key, @NotNull List<E> defaultValue, @NotNull Class<E> enumClass) {
        DetectPropertyBuilder<EnumListProperty<E>, EnumListDetectProperty<E>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new EnumListDetectProperty<E>(key, defaultValue, enumClass));
        return builder;
    }
}
