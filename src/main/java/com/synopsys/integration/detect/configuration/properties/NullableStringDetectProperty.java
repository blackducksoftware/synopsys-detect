package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;

public class NullableStringDetectProperty extends DetectProperty<NullableStringProperty> {
    public NullableStringDetectProperty(String key) {
        super(new NullableStringProperty(key));
    }

    public static DetectPropertyBuilder<NullableStringProperty, NullableStringDetectProperty> newBuilder(String key) {
        DetectPropertyBuilder<NullableStringProperty, NullableStringDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new NullableStringDetectProperty(key));
        return builder;
    }
}
