package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;

public class NullablePathDetectProperty extends DetectProperty<NullablePathProperty> {
    public NullablePathDetectProperty(String key) {
        super(new NullablePathProperty(key));
    }

    public static DetectPropertyBuilder<NullablePathProperty, NullablePathDetectProperty> newBuilder(String key) {
        DetectPropertyBuilder<NullablePathProperty, NullablePathDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new NullablePathDetectProperty(key));
        return builder;
    }
}
