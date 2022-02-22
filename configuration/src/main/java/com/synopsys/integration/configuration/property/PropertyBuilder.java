package com.synopsys.integration.configuration.property;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.deprecation.PropertyRemovalDeprecationInfo;
import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;
import com.synopsys.integration.configuration.util.ProductMajorVersion;

public class PropertyBuilder<P extends Property> {
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
    private PropertyRemovalDeprecationInfo propertyRemovalDeprecationInfo = null;
    @Nullable
    private String example = null;

    private Supplier<P> creator;

    public PropertyBuilder<P> setInfo(String name, PropertyVersion fromVersion) {
        this.name = name;
        this.fromVersion = fromVersion.getVersion();
        return this;
    }

    public PropertyBuilder<P> setHelp(@NotNull String shortText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, null);
        return this;
    }

    public PropertyBuilder<P> setHelp(@NotNull String shortText, @Nullable String longText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, longText);
        return this;
    }

    public PropertyBuilder<P> setGroups(Group primaryGroup, Group... additionalGroups) {
        this.propertyGroupInfo = new PropertyGroupInfo(primaryGroup, additionalGroups);
        return this;
    }

    public PropertyBuilder<P> setCategory(Category category) {
        this.category = category;
        return this;
    }

    public PropertyBuilder<P> setDeprecated(String description, ProductMajorVersion removeInVersion) {
        this.propertyRemovalDeprecationInfo = new PropertyRemovalDeprecationInfo(description, removeInVersion);
        return this;
    }

    public PropertyBuilder<P> setExample(String example) {
        this.example = example;
        return this;
    }

    public PropertyBuilder<P> setCreator(Supplier<P> creator) {
        this.creator = creator;
        return this;
    }

    public P build() {
        P detectProperty = creator.get();
        detectProperty.setCategory(category);
        detectProperty.setGroups(propertyGroupInfo);
        detectProperty.setRemovalDeprecation(propertyRemovalDeprecationInfo);
        detectProperty.setExample(example);
        assert propertyHelpInfo != null;
        detectProperty.setHelp(propertyHelpInfo);
        assert fromVersion != null;
        detectProperty.setInfo(name, fromVersion);
        return detectProperty;
    }

}