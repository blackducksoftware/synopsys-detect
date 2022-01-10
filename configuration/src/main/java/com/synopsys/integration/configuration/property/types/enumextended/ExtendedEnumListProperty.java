package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;
import com.synopsys.integration.configuration.util.PropertyUtils;

public class ExtendedEnumListProperty<E extends Enum<E>, B extends Enum<B>> extends ValuedListProperty<ExtendedEnumValue<E, B>> {
    private final List<String> allOptions;
    protected final Class<B> bClass;
    protected final Class<E> eClass;

    public ExtendedEnumListProperty(@NotNull String key, @NotNull List<ExtendedEnumValue<E, B>> defaultValue, @NotNull Class<E> eClass, @NotNull Class<B> bClass) {
        super(key, new ListValueParser<>(new ExtendedEnumValueParser<>(eClass, bClass)), defaultValue);

        allOptions = new ArrayList<>();
        allOptions.addAll(EnumPropertyUtils.getEnumNames(eClass));
        allOptions.addAll(EnumPropertyUtils.getEnumNames(bClass));
        this.bClass = bClass;
        this.eClass = eClass;
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
        return allOptions;
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }

    @Nullable
    @Override
    public String describeType() {
        return bClass.getSimpleName() + " List";
    }
}
