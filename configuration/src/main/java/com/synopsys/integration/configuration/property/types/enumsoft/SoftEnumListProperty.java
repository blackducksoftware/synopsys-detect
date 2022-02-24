package com.synopsys.integration.configuration.property.types.enumsoft;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedAlikeListProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;
import com.synopsys.integration.configuration.util.PropertyUtils;

public class SoftEnumListProperty<E extends Enum<E>> extends ValuedAlikeListProperty<SoftEnumValue<E>> {
    @NotNull
    private final Class<E> enumClass;

    public SoftEnumListProperty(@NotNull String key, List<SoftEnumValue<E>> defaultValue, @NotNull Class<E> enumClass) {
        super(key, new ListValueParser<>(new SoftEnumValueParser<>(enumClass)), defaultValue);
        this.enumClass = enumClass;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNames(enumClass);
    }

    @Override
    public boolean isOnlyExampleValues() {
        return false;
    }

    @Nullable
    @Override
    public String describeType() {
        return enumClass.getSimpleName() + " List";
    }
}