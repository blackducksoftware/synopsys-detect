package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.bool.NullableBooleanProperty;

public class NullableBooleanDetectProperty extends DetectProperty<NullableBooleanProperty> {
    public NullableBooleanDetectProperty(String key) {
        super(new NullableBooleanProperty(key));
    }

    public static DetectPropertyBuilder<NullableBooleanProperty, NullableBooleanDetectProperty> newBuilder(String key) {
        DetectPropertyBuilder<NullableBooleanProperty, NullableBooleanDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new NullableBooleanDetectProperty(key));
        return builder;
    }
}
