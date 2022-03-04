package com.synopsys.integration.configuration.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;
import com.synopsys.integration.configuration.property.deprecation.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.deprecation.PropertyRemovalDeprecationInfo;
import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;
import com.synopsys.integration.configuration.util.ProductMajorVersion;

/**
 * This is the most basic property.
 * It has no type information and a value cannot be retrieved for it (without a subclass).
 **/
public abstract class Property {
    public Property(String key) {
        this.key = key;
    }

    @NotNull
    private final String key;
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
    @NotNull
    private final PropertyDeprecationInfo propertyDeprecationInfo = new PropertyDeprecationInfo();
    @Nullable
    private String example = null;

    public Property setInfo(String name, String fromVersion) {
        this.name = name;
        this.fromVersion = fromVersion;
        return this;
    }

    public Property setHelp(@NotNull String shortText) {
        return setHelp(new PropertyHelpInfo(shortText, null));
    }

    public Property setHelp(@NotNull String shortText, @Nullable String longText) {
        return setHelp(new PropertyHelpInfo(shortText, longText));
    }

    public Property setHelp(PropertyHelpInfo propertyHelpInfo) {
        this.propertyHelpInfo = propertyHelpInfo;
        return this;
    }

    public Property setGroups(Group primaryGroup, Group... additionalGroups) {
        return setGroups(new PropertyGroupInfo(primaryGroup, additionalGroups));
    }

    public Property setGroups(PropertyGroupInfo propertyGroupInfo) {
        this.propertyGroupInfo = propertyGroupInfo;
        return this;
    }

    public Property setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Property setRemovalDeprecation(String description, ProductMajorVersion removeInVersion) {
        propertyDeprecationInfo.setRemovalDeprecation(description, removeInVersion);
        return this;
    }

    public void addDeprecatedValueInfo(String valueDescription, String reason) {
        propertyDeprecationInfo.addDeprecatedValueInfo(valueDescription, reason);
    }

    protected Optional<DeprecatedValueUsage> createDeprecatedValueUsageIfExists(String valueDescription) {
        return getPropertyDeprecationInfo().getDeprecatedValues().stream()
            .filter(info -> info.getValueDescription().equals(valueDescription))
            .findFirst()
            .map(info -> new DeprecatedValueUsage(valueDescription, info));
    }

    public Property setExample(String example) {
        this.example = example;
        return this;
    }

    public boolean isCaseSensitive() {
        return false;
    }

    public boolean isOnlyExampleValues() {
        return false;
    }

    public List<String> listExampleValues() {
        return new ArrayList<>();
    }

    public String describeType() {
        return null;
    }

    public String describeDefault() {
        return null;
    }

    public boolean isCommaSeparated() {
        return false;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public PropertyHelpInfo getPropertyHelpInfo() {
        return propertyHelpInfo;
    }

    public PropertyGroupInfo getPropertyGroupInfo() {
        return propertyGroupInfo;
    }

    public Category getCategory() {
        return category;
    }

    public PropertyDeprecationInfo getPropertyDeprecationInfo() {
        return propertyDeprecationInfo;
    }

    public boolean isDeprecatedForRemoval() {
        return propertyDeprecationInfo.getRemovalInfo().isPresent();
    }

    public String getKeyAsEnvironmentVariable() {
        if (StringUtils.isNotBlank(key)) {
            return key.replace(".", "_").toUpperCase();
        }

        return key;
    }

    public String getExample() {
        return example;
    }

    public void setRemovalDeprecation(PropertyRemovalDeprecationInfo propertyRemovalDeprecationInfo) {
        this.propertyDeprecationInfo.setRemovalDeprecation(propertyRemovalDeprecationInfo);
    }
}



