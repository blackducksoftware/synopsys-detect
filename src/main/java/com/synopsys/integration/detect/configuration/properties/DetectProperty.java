package com.synopsys.integration.detect.configuration.properties;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyGroupInfo;
import com.synopsys.integration.configuration.property.PropertyHelpInfo;
import com.synopsys.integration.configuration.property.deprecation.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.util.Category;

public class DetectProperty<T extends Property> {
    private final T property;

    @Nullable
    private String example = null;

    public DetectProperty(T property) {
        this.property = property;
    }

    public DetectProperty<T> setExample(String example) {
        this.example = example;
        return this;
    }

    public T getProperty() {
        return property;
    }

    @Nullable
    public String getName() {
        return getProperty().getName();
    }

    @Nullable
    public String getFromVersion() {
        return getProperty().getFromVersion();
    }

    @Nullable
    public PropertyHelpInfo getPropertyHelpInfo() {
        return getProperty().getPropertyHelpInfo();
    }

    @Nullable
    public PropertyGroupInfo getPropertyGroupInfo() {
        return getProperty().getPropertyGroupInfo();
    }

    @Nullable
    public Category getCategory() {
        return getProperty().getCategory();
    }

    @Nullable
    public PropertyDeprecationInfo getPropertyDeprecationInfo() {
        return getProperty().getPropertyDeprecationInfo();
    }

    @Nullable
    public String getExample() {
        return example;
    }

    public String getKey() {
        return getProperty().getKey();
    }
}
