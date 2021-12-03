package com.synopsys.integration.configuration.property.types.enumfilterable;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class FilterableEnumProperty<E extends Enum<E>> extends ValuedProperty<FilterableEnumValue<E>> {
    @NotNull
    private final Class<E> enumClass;

    public FilterableEnumProperty(@NotNull String key, @NotNull FilterableEnumValue<E> defaultValue, @NotNull Class<E> enumClass) {
        super(key, new FilterableEnumValueParser<>(enumClass), defaultValue);
        this.enumClass = enumClass;
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNamesAnd(enumClass, "ALL", "NONE");
    }

    @Nullable
    @Override
    public String describeType() {
        return enumClass.getSimpleName();
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }
}
