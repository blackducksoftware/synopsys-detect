package com.synopsys.integration.detect.configuration.properties;

import java.util.List;

import com.synopsys.integration.configuration.property.types.string.StringListProperty;

public class StringListDetectProperty extends DetectProperty<StringListProperty> {
    public StringListDetectProperty(String key, List<String> defaultValue) {
        super(new StringListProperty(key, defaultValue));
    }

    public static DetectPropertyBuilder<StringListProperty, StringListDetectProperty> newBuilder(String key, List<String> defaultValue) {
        DetectPropertyBuilder<StringListProperty, StringListDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new StringListDetectProperty(key, defaultValue));
        return builder;
    }
}
