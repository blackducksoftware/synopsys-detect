package com.synopsys.integration.detect.configuration.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.PropertyGroupInfo;
import com.synopsys.integration.configuration.property.PropertyHelpInfo;
import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;
import com.synopsys.integration.configuration.util.ProductMajorVersion;
import com.synopsys.integration.detect.configuration.DetectPropertyFromVersion;

public class DetectProperty<T extends Property> {
    private final T property;

    @Nullable
    private String name = null;
    @Nullable
    private String fromVersion = null;
    @Nullable
    private PropertyHelpInfo propertyHelpInfo = null;
    @Nullable
    private PropertyGroupInfo propertyGroupInfo = null;
    @Nullable
    private Category category = null;
    @Nullable
    private PropertyDeprecationInfo propertyDeprecationInfo = null;
    @Nullable
    private String example = null;

    public DetectProperty(T property) {
        this.property = property;
    }

    public DetectProperty<T> setInfo(String name, DetectPropertyFromVersion fromVersion) {
        this.name = name;
        this.fromVersion = fromVersion.getVersion();
        return this;
    }

    public DetectProperty<T> setHelp(@NotNull PropertyHelpInfo propertyHelpInfo) {
        this.propertyHelpInfo = propertyHelpInfo;
        return this;
    }

    public DetectProperty<T> setGroups(PropertyGroupInfo propertyGroupInfo) {
        this.propertyGroupInfo = propertyGroupInfo;
        return this;
    }

    public DetectProperty<T> setCategory(Category category) {
        this.category = category;
        return this;
    }

    public DetectProperty<T> setDeprecated(PropertyDeprecationInfo propertyDeprecationInfo) {
        this.propertyDeprecationInfo = propertyDeprecationInfo;
        return this;
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
        return name;
    }

    @Nullable
    public String getFromVersion() {
        return fromVersion;
    }

    @Nullable
    public PropertyHelpInfo getPropertyHelpInfo() {
        return propertyHelpInfo;
    }

    @Nullable
    public PropertyGroupInfo getPropertyGroupInfo() {
        return propertyGroupInfo;
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    @Nullable
    public PropertyDeprecationInfo getPropertyDeprecationInfo() {
        return propertyDeprecationInfo;
    }

    @Nullable
    public String getExample() {
        return example;
    }
}
