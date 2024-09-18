package com.blackduck.integration.configuration.property.deprecation;

import com.blackduck.integration.configuration.util.ProductMajorVersion;
import org.jetbrains.annotations.NotNull;

public class PropertyRemovalDeprecationInfo {
    @NotNull
    private final String description;
    @NotNull
    private final ProductMajorVersion removeInVersion;
    @NotNull
    private final String deprecationText;

    public PropertyRemovalDeprecationInfo(@NotNull String description, @NotNull ProductMajorVersion removeInVersion) {
        this.description = description;
        this.removeInVersion = removeInVersion;
        this.deprecationText = getDescription() + " This property will be removed in " + getRemoveInVersion().getDisplayValue() + ".";
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public ProductMajorVersion getRemoveInVersion() {
        return removeInVersion;
    }

    @NotNull
    public String getDeprecationText() {
        return deprecationText;
    }
}
