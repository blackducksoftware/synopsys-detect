package com.synopsys.integration.configuration.property.types.enumallnone.property;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.enumallnone.list.NoneEnumList;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.NoneEnum;

public class NoneEnumListProperty<B extends Enum<B>> extends ExtendedEnumListProperty<NoneEnum, B> {
    public NoneEnumListProperty(@NotNull String key, List<ExtendedEnumValue<NoneEnum, B>> defaultValue, @NotNull Class<B> eClass) {
        super(key, defaultValue, NoneEnum.class, eClass);
    }

    public NoneEnumListProperty(@NotNull String key, @NotNull NoneEnum noneValue, @NotNull Class<B> eClass) {
        super(key, Collections.singletonList(ExtendedEnumValue.ofExtendedValue(noneValue)), NoneEnum.class, eClass);
    }

    public NoneEnumListProperty(@NotNull String key, @NotNull B extendedValue, @NotNull Class<B> eClass) {
        super(key, Collections.singletonList(ExtendedEnumValue.ofBaseValue(extendedValue)), NoneEnum.class, eClass);
    }

    public NoneEnumList<B> toList(List<ExtendedEnumValue<NoneEnum, B>> values) {
        return new NoneEnumList<B>(values, bClass);
    }
}
