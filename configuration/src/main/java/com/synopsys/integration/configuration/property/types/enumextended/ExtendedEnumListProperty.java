package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;
import com.synopsys.integration.configuration.util.PropertyUtils;

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
