package com.synopsys.integration.configuration.property;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;

public class PropertyBuilder<T extends Property> {

    @NotNull
    private final String key;
    @Nullable
    private String defaultValue = null;
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

    public PropertyBuilder(String key) {
        this.key = key;
    }

    public PropertyBuilder(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public PropertyBuilder(String key, List<String> defaultValues) {
        this.key = key;
        this.defaultValue = defaultValues;
    }

    public PropertyBuilder(String key, Integer defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public PropertyBuilder(String key, Boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public T build(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T property = clazz.newInstance();
        if (defaultValue != null) {
            property.setDefault(defaultValue);
        }
        if (name != null && fromVersion != null) {
            property.setInfo(name, fromVersion);
        }
        if (propertyHelpInfo != null) {
            property.setHelp(propertyHelpInfo.getShortText(), propertyHelpInfo.getLongText());
        }
        if (propertyGroupInfo != null) {
            property.setGroups(propertyGroupInfo.getPrimaryGroup(), propertyGroupInfo.getAdditionalGroups().toArray(new Group[0]));
        }
        if (category != null) {
            property.setCategory(category);
        }

        return property;
    }

    public PropertyBuilder<T> info(String name, String version) {
        this.name = name;
        this.fromVersion = version;
        return this;
    }

    public PropertyBuilder<T> help(String help) {
        this.propertyHelpInfo = new PropertyHelpInfo(help, help);
        return this;
    }

    public PropertyBuilder<T> help(String shortText, String longText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, longText);
        return this;
    }

    public PropertyBuilder<T> groups(Group primaryGroup, Group... additionalGroups) {
        this.propertyGroupInfo = new PropertyGroupInfo(primaryGroup, additionalGroups);
        return this;
    }

    public PropertyBuilder<T> category(Category category) {
        this.category = category;
        return this;
    }
}
