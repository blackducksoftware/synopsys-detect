package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.integer.IntegerProperty;
import com.synopsys.integration.configuration.property.types.longs.LongProperty;

public class LongDetectProperty extends DetectProperty<LongProperty> {
    public LongDetectProperty(String key, Long defaultValue) {
        super(new LongProperty(key, defaultValue));
    }

    public static DetectPropertyBuilder<LongProperty, LongDetectProperty> newBuilder(String key, Long defaultValue) {
        DetectPropertyBuilder<LongProperty, LongDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new LongDetectProperty(key, defaultValue));
        return builder;
    }
}
