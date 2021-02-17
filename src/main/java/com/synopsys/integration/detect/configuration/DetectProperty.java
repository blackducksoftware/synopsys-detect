/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.PropertyGroupInfo;
import com.synopsys.integration.configuration.property.PropertyHelpInfo;
import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;
import com.synopsys.integration.configuration.util.ProductMajorVersion;

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

    public DetectProperty<T> setHelp(@NotNull String shortText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, null);
        return this;
    }

    public DetectProperty<T> setHelp(@NotNull String shortText, @Nullable String longText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, longText);
        return this;
    }

    public DetectProperty<T> setGroups(Group primaryGroup, Group... additionalGroups) {
        this.propertyGroupInfo = new PropertyGroupInfo(primaryGroup, additionalGroups);
        return this;
    }

    public DetectProperty<T> setCategory(Category category) {
        this.category = category;
        return this;
    }

    public DetectProperty<T> setDeprecated(String description, ProductMajorVersion failInVersion, ProductMajorVersion removeInVersion) {
        this.propertyDeprecationInfo = new PropertyDeprecationInfo(description, failInVersion, removeInVersion);
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
