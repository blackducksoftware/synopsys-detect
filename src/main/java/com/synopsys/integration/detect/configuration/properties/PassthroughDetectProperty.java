package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.base.PassthroughProperty;
import com.synopsys.integration.configuration.property.types.string.StringProperty;

public class PassthroughDetectProperty extends DetectProperty<PassthroughProperty> {
    public PassthroughDetectProperty(String key) {
        super(new PassthroughProperty(key));
    }

    public static DetectPropertyBuilder<PassthroughProperty, PassthroughDetectProperty> newBuilder(String key) {
        DetectPropertyBuilder<PassthroughProperty, PassthroughDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new PassthroughDetectProperty(key));
        return builder;
    }
}
