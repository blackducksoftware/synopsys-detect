package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;

public class ExtendedEnumProperty<E extends Enum<E>, B extends Enum<B>> extends ValuedProperty<ExtendedEnumValue<E, B>> {
    private List<String> allOptions;
    private Class<B> bClass;

    public ExtendedEnumProperty(@NotNull final String key,
        @NotNull final Class<E> eClass,
        @NotNull final Class<B> bClass,
        @NotNull ExtendedEnumValue<E, B> defaultValue) {
        super(key, new ExtendedEnumValueParser<E, B>(eClass, bClass), defaultValue);
        allOptions = new ArrayList<>();
        allOptions.addAll(EnumPropertyUtils.getEnumNames(eClass));
        allOptions.addAll(EnumPropertyUtils.getEnumNames(bClass));
        this.bClass = bClass;
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return allOptions;
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Nullable
    @Override
    public String describeType() {
        return bClass.getSimpleName() + " List";
    }
}
