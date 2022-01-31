package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class ExtendedEnumListProperty<E extends Enum<E>, B extends Enum<B>> extends ExtendedEnumListPropertyBase<E, B, List<ExtendedEnumValue<E, B>>> {
    public ExtendedEnumListProperty(@NotNull String key, @NotNull List<ExtendedEnumValue<E, B>> defaultValue, @NotNull Class<E> eClass, @NotNull Class<B> bClass) {
        super(key, defaultValue, eClass, bClass);
    }

    @NotNull
    @Override
    public List<ExtendedEnumValue<E, B>> convertValue(List<ExtendedEnumValue<E, B>> value) {
        return value;
    }
}
