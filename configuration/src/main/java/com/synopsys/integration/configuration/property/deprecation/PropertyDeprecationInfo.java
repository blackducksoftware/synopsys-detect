package com.synopsys.integration.configuration.property.deprecation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.util.ProductMajorVersion;

public class PropertyDeprecationInfo {
    @Nullable
    private PropertyRemovalDeprecationInfo removalDeprecationInfo = null;
    @NotNull
    private List<DeprecatedValueInfo> deprecatedValues = new ArrayList<>();

    public Optional<PropertyRemovalDeprecationInfo> getRemovalInfo() {
        return Optional.ofNullable(removalDeprecationInfo);
    }

    public void setRemovalDeprecation(String description, ProductMajorVersion removeInVersion) {
        setRemovalDeprecation(new PropertyRemovalDeprecationInfo(description, removeInVersion));
    }

    public void setRemovalDeprecation(PropertyRemovalDeprecationInfo propertyRemovalDeprecationInfo) {
        this.removalDeprecationInfo = propertyRemovalDeprecationInfo;
    }

    public List<DeprecatedValueInfo> getDeprecatedValues() {
        return deprecatedValues;
    }

    public void setDeprecatedValues(List<DeprecatedValueInfo> deprecatedValues) {
        this.deprecatedValues = deprecatedValues;
    }

    public void addDeprecatedValueInfo(String valueDescription, String reason) {
        deprecatedValues.add(new DeprecatedValueInfo(valueDescription, reason));
    }
}
