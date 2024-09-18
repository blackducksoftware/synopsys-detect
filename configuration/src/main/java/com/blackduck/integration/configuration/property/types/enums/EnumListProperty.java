package com.blackduck.integration.configuration.property.types.enums;

import java.util.List;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.PropertyBuilder;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;
import com.blackduck.integration.configuration.util.EnumPropertyUtils;
import com.blackduck.integration.configuration.util.PropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumListProperty<E extends Enum<E>> extends ValuedAlikeListProperty<E> {
    @NotNull
    private final Class<E> enumClass;

    public EnumListProperty(@NotNull String key, @NotNull List<E> defaultValue, @NotNull Class<E> enumClass) {
        super(key, new ListValueParser<>(new EnumValueParser<>(enumClass)), defaultValue);
        this.enumClass = enumClass;
    }

    public static <E extends Enum<E>> PropertyBuilder<EnumListProperty<E>> newBuilder(@NotNull String key, @NotNull List<E> defaultValue, @NotNull Class<E> enumClass) {
        return new PropertyBuilder<EnumListProperty<E>>().setCreator(() -> new EnumListProperty<>(key, defaultValue, enumClass));
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
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
        return enumClass.getSimpleName() + " List";
    }
}