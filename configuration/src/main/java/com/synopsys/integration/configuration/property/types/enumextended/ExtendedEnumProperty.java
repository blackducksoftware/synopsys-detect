package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;
import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class ExtendedEnumProperty<E extends Enum<E>, B extends Enum<B>> extends ValuedAlikeProperty<ExtendedEnumValue<E, B>> {
    private final List<String> allOptions;
    private final Class<B> bClass;

    public ExtendedEnumProperty(@NotNull String key, @NotNull ExtendedEnumValue<E, B> defaultValue, @NotNull Class<E> eClass, @NotNull Class<B> bClass) {
        super(key, new ExtendedEnumValueParser<>(eClass, bClass), defaultValue);
        allOptions = new ArrayList<>();
        allOptions.addAll(EnumPropertyUtils.getEnumNames(eClass));
        allOptions.addAll(EnumPropertyUtils.getEnumNames(bClass));
        this.bClass = bClass;
    }

    public static <E extends Enum<E>, B extends Enum<B>> PropertyBuilder<ExtendedEnumProperty<E, B>> newBuilder(
        @NotNull String key,
        @NotNull ExtendedEnumValue<E, B> defaultValue,
        @NotNull Class<E> eClass,
        @NotNull Class<B> bClass
    ) {
        return new PropertyBuilder<ExtendedEnumProperty<E, B>>().setCreator(() -> new ExtendedEnumProperty<>(key, defaultValue, eClass, bClass));
    }

    public static <E extends Enum<E>, B extends Enum<B>> PropertyBuilder<ExtendedEnumProperty<E, B>> newBuilderExtendedDefault(
        @NotNull String key,
        @NotNull E defaultValue,
        @NotNull Class<E> eClass,
        @NotNull Class<B> bClass
    ) {
        return newBuilder(key, ExtendedEnumValue.ofExtendedValue(defaultValue), eClass, bClass);
    }

    public static <E extends Enum<E>, B extends Enum<B>> PropertyBuilder<ExtendedEnumProperty<E, B>> newBuilderBaseDefault(
        @NotNull String key,
        @NotNull B defaultValue,
        @NotNull Class<E> eClass,
        @NotNull Class<B> bClass
    ) {
        return newBuilder(key, ExtendedEnumValue.ofBaseValue(defaultValue), eClass, bClass);
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
        return bClass.getSimpleName();
    }

    private final List<B> deprecatedValues = new ArrayList<>();
    @NotNull
    public ExtendedEnumProperty<E,B> deprecateValue(B value, String reason) {
        deprecatedValues.add(value);
        addDeprecatedValueInfo(value.toString(), reason);
        return this;
    }

    @Override
    @NotNull
    public List<DeprecatedValueUsage> checkForDeprecatedValues(ExtendedEnumValue<E, B> value) {
        if (getPropertyDeprecationInfo() == null)
            return Collections.emptyList();
        if (getPropertyDeprecationInfo().getDeprecatedValues() == null)
            return Collections.emptyList();

        List<DeprecatedValueUsage> usages = new ArrayList<>();
        if (value.getBaseValue().isPresent() && deprecatedValues.contains(value.getBaseValue().get())) {
            createDeprecatedValueUsageIfExists(value.getBaseValue().get().toString())
                    .ifPresent(usages::add);
        }
        return usages;
    }
}
