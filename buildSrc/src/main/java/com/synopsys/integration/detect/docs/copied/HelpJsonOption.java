/*
 * buildSrc
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
package com.synopsys.integration.detect.docs.copied;

import java.util.ArrayList;
import java.util.List;

// Copied from detect-configuration
public class HelpJsonOption {
    private String propertyName = "";
    private String propertyKey = "";
    private String propertyType = "";
    private String defaultValue = "";
    private String addedInVersion = "";
    private String category = "";
    private String group = "";
    private String superGroup = "";
    private List<String> additionalGroups = new ArrayList<>();
    private String description = "";
    private String detailedDescription = "";
    private Boolean deprecated = false;
    private String deprecatedDescription = "";
    private String deprecatedFailInVersion = "";
    private String deprecatedRemoveInVersion = "";
    private Boolean strictValues = false;
    private Boolean caseSensitiveValues = false;
    private Boolean hasAcceptableValues = false;
    private Boolean isCommaSeparatedList = false;
    private List<String> acceptableValues = new ArrayList<>();

    //This is added for use in the markdown, it does not actually exist on the object.
    private String location = "";

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(final String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAddedInVersion() {
        return addedInVersion;
    }

    public void setAddedInVersion(final String addedInVersion) {
        this.addedInVersion = addedInVersion;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public String getSuperGroup() {
        return superGroup;
    }

    public void setSuperGroup(final String superGroup) {
        this.superGroup = superGroup;
    }

    public List<String> getAdditionalGroups() {
        return additionalGroups;
    }

    public void setAdditionalGroups(final List<String> additionalGroups) {
        this.additionalGroups = additionalGroups;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(final String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(final Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getDeprecatedDescription() {
        return deprecatedDescription;
    }

    public void setDeprecatedDescription(final String deprecatedDescription) {
        this.deprecatedDescription = deprecatedDescription;
    }

    public String getDeprecatedFailInVersion() {
        return deprecatedFailInVersion;
    }

    public void setDeprecatedFailInVersion(final String deprecatedFailInVersion) {
        this.deprecatedFailInVersion = deprecatedFailInVersion;
    }

    public String getDeprecatedRemoveInVersion() {
        return deprecatedRemoveInVersion;
    }

    public void setDeprecatedRemoveInVersion(final String deprecatedRemoveInVersion) {
        this.deprecatedRemoveInVersion = deprecatedRemoveInVersion;
    }

    public Boolean getStrictValues() {
        return strictValues;
    }

    public void setStrictValues(final Boolean strictValues) {
        this.strictValues = strictValues;
    }

    public Boolean getCaseSensitiveValues() {
        return caseSensitiveValues;
    }

    public void setCaseSensitiveValues(final Boolean caseSensitiveValues) {
        this.caseSensitiveValues = caseSensitiveValues;
    }

    public Boolean getHasAcceptableValues() {
        return hasAcceptableValues;
    }

    public void setHasAcceptableValues(final Boolean hasAcceptableValues) {
        this.hasAcceptableValues = hasAcceptableValues;
    }

    public Boolean getCommaSeparatedList() {
        return isCommaSeparatedList;
    }

    public void setCommaSeparatedList(final Boolean commaSeparatedList) {
        isCommaSeparatedList = commaSeparatedList;
    }

    public List<String> getAcceptableValues() {
        return acceptableValues;
    }

    public void setAcceptableValues(final List<String> acceptableValues) {
        this.acceptableValues = acceptableValues;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }
}
