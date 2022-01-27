package com.synopsys.integration.detect.configuration.properties;

import java.util.List;

import com.synopsys.integration.configuration.property.types.string.CaseSensitiveStringListProperty;
import com.synopsys.integration.configuration.property.types.string.StringListProperty;

public class CaseSensitiveStringListDetectProperty extends DetectProperty<CaseSensitiveStringListProperty> {
    public CaseSensitiveStringListDetectProperty(String key) {
        super(new CaseSensitiveStringListProperty(key));
    }

    public static DetectPropertyBuilder<CaseSensitiveStringListProperty, CaseSensitiveStringListDetectProperty> newBuilder(String key) {
        DetectPropertyBuilder<CaseSensitiveStringListProperty, CaseSensitiveStringListDetectProperty> builder = new DetectPropertyBuilder<>();
        builder.setCreator(() -> new CaseSensitiveStringListDetectProperty(key));
        return builder;
    }
}
