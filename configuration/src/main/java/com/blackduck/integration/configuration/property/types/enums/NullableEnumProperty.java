package com.blackduck.integration.configuration.property.types.enums;

import java.util.List;

import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import com.blackduck.integration.configuration.util.EnumPropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullableEnumProperty<E extends Enum<E>> extends NullableAlikeProperty<E> {
    @NotNull
    private final Class<E> enumClass;

    public NullableEnumProperty(@NotNull String key, @NotNull Class<E> enumClass) {
        super(key, new EnumValueParser<>(enumClass));
        this.enumClass = enumClass;
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
        return "Optional " + enumClass.getSimpleName();
    }
}
