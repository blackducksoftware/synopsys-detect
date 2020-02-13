package com.synopsys.integration.configuration.property.types.enumextended;

import org.antlr.v4.runtime.misc.Nullable;
import org.springframework.util.Assert;

public class ExtendedEnumValue<E extends Enum<E>, B extends Enum<B>> {
    @Nullable
    private E extendedValue;
    @Nullable
    private B baseValue;

    private ExtendedEnumValue(E extendedValue, B baseValue) {
        if (baseValue == null) {
            Assert.notNull(extendedValue, "You must provide either a base value or an extended value.");
        } else if (extendedValue == null) {
            Assert.notNull(baseValue, "You must provide either a base value or an extended value.");
        }
        Assert.isTrue(baseValue != null && extendedValue != null, "Only one value may be not null.");

        this.baseValue = baseValue;
        this.extendedValue = extendedValue;
    }

    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofBaseValue(B baseValue) {
        return new ExtendedEnumValue<E, B>(null, baseValue);
    }

    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofExtendedValue(E extendedValue) {
        return new ExtendedEnumValue<E, B>(extendedValue, null);
    }
}
