package com.synopsys.integration.detect.configuration.properties;

import java.util.List;

import com.synopsys.integration.configuration.property.types.path.PathListProperty;
import com.synopsys.integration.configuration.property.types.path.PathValue;

public class PathListDetectProperty extends DetectProperty<PathListProperty> {
    public PathListDetectProperty(String key, List<PathValue> defaultValue) {
        super(new PathListProperty(key, defaultValue));
    }

    public static DetectPropertyBuilder<PathListProperty, PathListDetectProperty> newBuilder(String key, List<PathValue> defaultValue) {
        DetectPropertyBuilder<PathListProperty, PathListDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new PathListDetectProperty(key, defaultValue));
        return builder;
    }
}
