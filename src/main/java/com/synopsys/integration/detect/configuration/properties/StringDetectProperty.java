package com.synopsys.integration.detect.configuration.properties;

import com.synopsys.integration.configuration.property.types.bool.BooleanProperty;
import com.synopsys.integration.configuration.property.types.string.StringProperty;

public class StringDetectProperty extends DetectProperty<StringProperty> {
    public StringDetectProperty(String key, String defaultValue) {
        super(new StringProperty(key, defaultValue));
    }

    public static DetectPropertyBuilder<StringProperty, StringDetectProperty> newBuilder(String key, String defaultValue) {
        DetectPropertyBuilder<StringProperty, StringDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new StringDetectProperty(key, defaultValue));
        return builder;
    }
}
