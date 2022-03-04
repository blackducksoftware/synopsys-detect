package com.synopsys.integration.configuration.property.types.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;
import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class EnumProperty<E extends Enum<E>> extends ValuedAlikeProperty<E> {
    @NotNull
    private final Class<E> enumClass;

    public EnumProperty(@NotNull String key, @NotNull E defaultValue, @NotNull Class<E> enumClass) {
        super(key, new EnumValueParser<>(enumClass), defaultValue);
        this.enumClass = enumClass;
    }

    public static <E extends Enum<E>> PropertyBuilder<EnumProperty<E>> newBuilder(@NotNull String key, @NotNull E defaultValue, @NotNull Class<E> enumClass) {
        return new PropertyBuilder<EnumProperty<E>>().setCreator(() -> new EnumProperty<>(key, defaultValue, enumClass));
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNames(enumClass);
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }

    @Nullable
    @Override
    public String describeType() {
        return enumClass.getSimpleName();
    }

    private final List<E> deprecatedValues = new ArrayList<>();

    @NotNull
    public EnumProperty<E> deprecateValue(E value, String reason) {
        deprecatedValues.add(value);
        addDeprecatedValueInfo(value.toString(), reason);
        return this;
    }

    @Override
    @NotNull
    public List<DeprecatedValueUsage> checkForDeprecatedValues(E value) {
        if (getPropertyDeprecationInfo() == null)
            return Collections.emptyList();
        if (getPropertyDeprecationInfo().getDeprecatedValues() == null)
            return Collections.emptyList();

        List<DeprecatedValueUsage> usages = new ArrayList<>();
        if (deprecatedValues.contains(value)) {
            createDeprecatedValueUsageIfExists(value.toString())
                .ifPresent(usages::add);
        }
        return usages;
    }
}
