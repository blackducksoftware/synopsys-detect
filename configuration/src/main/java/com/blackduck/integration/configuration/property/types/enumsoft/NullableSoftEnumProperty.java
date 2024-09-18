package com.blackduck.integration.configuration.property.types.enumsoft;

import java.util.List;

import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import com.blackduck.integration.configuration.util.EnumPropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullableSoftEnumProperty<E extends Enum<E>> extends NullableAlikeProperty<SoftEnumValue<E>> {
    @NotNull
    private final Class<E> enumClass;

    public NullableSoftEnumProperty(@NotNull String key, @NotNull Class<E> enumClass) {
        super(key, new SoftEnumValueParser<>(enumClass));
        this.enumClass = enumClass;
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
        return "Optional " + enumClass.getSimpleName();
    }
}
