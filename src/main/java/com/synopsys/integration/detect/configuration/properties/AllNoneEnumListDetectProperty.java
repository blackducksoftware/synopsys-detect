package com.synopsys.integration.detect.configuration.properties;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.types.bool.BooleanProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.AllNoneEnum;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllNoneEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;

public class AllNoneEnumListDetectProperty<B extends Enum<B>> extends DetectProperty<AllNoneEnumListProperty<B>> {
    public AllNoneEnumListDetectProperty(@NotNull String key, List<ExtendedEnumValue<AllNoneEnum, B>> defaultValue, @NotNull Class<B> bClass) {
        super(new AllNoneEnumListProperty<B>(key, defaultValue, bClass));
    }

    public AllNoneEnumListDetectProperty(@NotNull String key, @NotNull AllNoneEnum allValue, @NotNull Class<B> bClass) {
        super(new AllNoneEnumListProperty<B>(key, allValue, bClass));
    }

    public static <B extends Enum<B>> DetectPropertyBuilder<AllNoneEnumListProperty<B>, AllNoneEnumListDetectProperty<B>> newBuilder(String key, List<ExtendedEnumValue<AllNoneEnum, B>> defaultValue, @NotNull Class<B> bClass) {
        DetectPropertyBuilder<AllNoneEnumListProperty<B>, AllNoneEnumListDetectProperty<B>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new AllNoneEnumListDetectProperty<B>(key, defaultValue, bClass));
        return builder;
    }

    public static <B extends Enum<B>> DetectPropertyBuilder<AllNoneEnumListProperty<B>, AllNoneEnumListDetectProperty<B>> newBuilder(String key, @NotNull AllNoneEnum allValue, @NotNull Class<B> bClass) {
        DetectPropertyBuilder<AllNoneEnumListProperty<B>, AllNoneEnumListDetectProperty<B>> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new AllNoneEnumListDetectProperty<B>(key, allValue, bClass));
        return builder;
    }

    public AllNoneEnumListDetectProperty<B> deprecateNone(String reason) {
        getProperty().deprecateNone(reason);
        return this;
    }
}