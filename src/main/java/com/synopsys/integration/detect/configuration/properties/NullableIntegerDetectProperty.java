package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.bool.NullableBooleanProperty;
import com.synopsys.integration.configuration.property.types.integer.NullableIntegerProperty;

public class NullableIntegerDetectProperty extends DetectProperty<NullableIntegerProperty> {
    public NullableIntegerDetectProperty(String key) {
        super(new NullableIntegerProperty(key));
    }

    public static DetectPropertyBuilder<NullableIntegerProperty, NullableIntegerDetectProperty> newBuilder(String key) {
        DetectPropertyBuilder<NullableIntegerProperty, NullableIntegerDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new NullableIntegerDetectProperty(key));
        return builder;
    }
}
