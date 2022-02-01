package com.synopsys.integration.detect.configuration.properties;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.AllNoneEnum;
import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.NoneEnum;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllNoneEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.property.NoneEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;

public class NoneEnumListDetectProperty<B extends Enum<B>> extends DetectProperty<NoneEnumListProperty<B>> {
    public NoneEnumListDetectProperty(@NotNull String key, List<ExtendedEnumValue<NoneEnum, B>> defaultValue, @NotNull Class<B> bClass) {
        super(new NoneEnumListProperty<B>(key, defaultValue, bClass));
    }

    public NoneEnumListDetectProperty(@NotNull String key, @NotNull NoneEnum allValue, @NotNull Class<B> bClass) {
        super(new NoneEnumListProperty<B>(key, allValue, bClass));
    }

    public static <B extends Enum<B>> DetectPropertyBuilder<NoneEnumListProperty<B>, NoneEnumListDetectProperty<B>> newBuilder(String key, List<ExtendedEnumValue<NoneEnum, B>> defaultValue, @NotNull Class<B> bClass) {
        DetectPropertyBuilder<NoneEnumListProperty<B>, NoneEnumListDetectProperty<B>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new NoneEnumListDetectProperty<B>(key, defaultValue, bClass));
        return builder;
    }

    public static <B extends Enum<B>> DetectPropertyBuilder<NoneEnumListProperty<B>, NoneEnumListDetectProperty<B>> newBuilder(@NotNull String key, @NotNull NoneEnum allValue, @NotNull Class<B> bClass) {
        DetectPropertyBuilder<NoneEnumListProperty<B>, NoneEnumListDetectProperty<B>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new NoneEnumListDetectProperty<B>(key, allValue, bClass));
        return builder;
    }
}