/*
 * configuration
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
package com.synopsys.integration.configuration.property;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private PropertyDeprecationInfo propertyDeprecationInfo = null;
    @Nullable
    private String example = null;

    public Property setInfo(String name, String fromVersion) {
        this.name = name;
        this.fromVersion = fromVersion;
        return this;
    }

    public Property setHelp(@NotNull String shortText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, null);
        return this;
    }

    public Property setHelp(@NotNull String shortText, @Nullable String longText) {
        this.propertyHelpInfo = new PropertyHelpInfo(shortText, longText);
        return this;
    }

    public Property setGroups(Group primaryGroup, Group... additionalGroups) {
        this.propertyGroupInfo = new PropertyGroupInfo(primaryGroup, additionalGroups);
        return this;
    }

    public Property setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Property setDeprecated(String description, ProductMajorVersion failInVersion, ProductMajorVersion removeInVersion) {
        this.propertyDeprecationInfo = new PropertyDeprecationInfo(description, failInVersion, removeInVersion);
        return this;
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

    public String getKeyAsEnvironmentVariable() {
        if (StringUtils.isNotBlank(key)) {
            return key.replace(".", "_").toUpperCase();
        }

        return key;
    }

    public String getExample() {
        return example;
    }
}



