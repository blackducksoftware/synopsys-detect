package com.synopsys.integration.detect.configuration.properties;

import java.util.List;

import com.synopsys.integration.configuration.property.types.integer.IntegerProperty;
import com.synopsys.integration.configuration.property.types.string.StringListProperty;

public class IntegerDetectProperty extends DetectProperty<IntegerProperty> {
    public IntegerDetectProperty(String key, Integer defaultValue) {
        super(new IntegerProperty(key, defaultValue));
    }

    public static DetectPropertyBuilder<IntegerProperty, IntegerDetectProperty> newBuilder(String key, Integer defaultValue) {
        DetectPropertyBuilder<IntegerProperty, IntegerDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new IntegerDetectProperty(key, defaultValue));
        return builder;
    }
}
