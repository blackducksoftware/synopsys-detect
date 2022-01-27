package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.bool.BooleanProperty;
import com.synopsys.integration.configuration.property.types.bool.NullableBooleanProperty;

public class BooleanDetectProperty extends DetectProperty<BooleanProperty> {
    public BooleanDetectProperty(String key, boolean defaultValue) {
        super(new BooleanProperty(key, defaultValue));
    }

    public static DetectPropertyBuilder<BooleanProperty, BooleanDetectProperty> newBuilder(String key, boolean defaultValue) {
        DetectPropertyBuilder<BooleanProperty, BooleanDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new BooleanDetectProperty(key, defaultValue));
        return builder;
    }
}
