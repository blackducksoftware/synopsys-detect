package com.synopsys.integration.configuration.property.types.enumextended;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.springframework.util.Assert;

public class ExtendedEnumValue<E extends Enum<E>, B extends Enum<B>> {
    @Nullable
    private final E extendedValue;
    @Nullable
    private final B baseValue;

    private ExtendedEnumValue(@Nullable final E extendedValue, @Nullable final B baseValue) {
        if (baseValue == null) {
            Assert.notNull(extendedValue, "You must provide either a base value or an extended value.");
        } else if (extendedValue == null) {
            Assert.notNull(baseValue, "You must provide either a base value or an extended value.");
        }
        Assert.isTrue(baseValue != null && extendedValue != null, "Only one value may be not null.");

        this.baseValue = baseValue;
        this.extendedValue = extendedValue;
    }

    @NotNull
    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofBaseValue(@NotNull final B baseValue) {
        return new ExtendedEnumValue<>(null, baseValue);
    }

    @NotNull
    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofExtendedValue(@NotNull final E extendedValue) {
        return new ExtendedEnumValue<>(extendedValue, null);
    }
}
