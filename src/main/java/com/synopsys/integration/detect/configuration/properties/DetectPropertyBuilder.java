package com.synopsys.integration.detect.configuration.properties;

import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.PropertyGroupInfo;
import com.synopsys.integration.configuration.property.PropertyHelpInfo;
import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;
import com.synopsys.integration.configuration.util.ProductMajorVersion;
import com.synopsys.integration.detect.configuration.DetectPropertyFromVersion;

public class DetectPropertyBuilder<P extends Property, T extends DetectProperty<P>> {
    @Nullable
    private String name = null;
    @Nullable
    private DetectPropertyFromVersion fromVersion = null;
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

    private Supplier<T> creator;

    public DetectPropertyBuilder<P, T> setInfo(String name, DetectPropertyFromVersion fromVersion) {
        this.name = name;
        this.fromVersion = fromVersion;
        return this;
    }

    public DetectPropertyBuilder<P, T> setHelp(@NotNull String shortText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, null);
        return this;
    }

    public DetectPropertyBuilder<P, T> setHelp(@NotNull String shortText, @Nullable String longText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, longText);
        return this;
    }

    public DetectPropertyBuilder<P, T> setGroups(Group primaryGroup, Group... additionalGroups) {
        this.propertyGroupInfo = new PropertyGroupInfo(primaryGroup, additionalGroups);
        return this;
    }

    public DetectPropertyBuilder<P, T> setCategory(Category category) {
        this.category = category;
        return this;
    }

    public DetectPropertyBuilder<P, T> setDeprecated(String description, ProductMajorVersion removeInVersion) {
        this.propertyDeprecationInfo = new PropertyDeprecationInfo(description, removeInVersion);
        return this;
    }

    public DetectPropertyBuilder<P, T> setExample(String example) {
        this.example = example;
        return this;
    }

    public void setCreator(Supplier<T> creator) {
        this.creator = creator;
    }

    public T build() {
        T detectProperty = creator.get();
        detectProperty.setCategory(category);
        detectProperty.setGroups(propertyGroupInfo);
        detectProperty.setDeprecated(propertyDeprecationInfo);
        detectProperty.setExample(example);
        assert propertyHelpInfo != null;
        detectProperty.setHelp(propertyHelpInfo);
        assert fromVersion != null;
        detectProperty.setInfo(name, fromVersion);
        return detectProperty;
    }

    ;
}
