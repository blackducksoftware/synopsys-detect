/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.help;

public class DetectOption {
    final String key;
    final String fieldName;
    final String description;
    final Class<?> valueType;
    final String group;
    final String originalValue;
    final String defaultValue;
    final String resolvedValue;
    public String interactiveValue = null;
    public DetailedDetectOption detailedDetectOption;

    public DetectOption(final String key, final String fieldName, final String originalValue, final String resolvedValue, final String description, final Class<?> valueType, final String defaultValue, final String group) {
        this.key = key;
        this.description = description;
        this.valueType = valueType;
        this.group = group;
        this.defaultValue = defaultValue;
        this.fieldName = fieldName;
        this.originalValue = originalValue;
        this.resolvedValue = resolvedValue;
        detailedDetectOption = new DetailedDetectOption();
    }

    public DetailedDetectOption getDetailedDetectOption() {
        return detailedDetectOption;
    }

    public void setDetailedDetectOption(final DetailedDetectOption detailedDetectOption) {
        this.detailedDetectOption = detailedDetectOption;
    }

    public String getInteractiveValue() {
        return interactiveValue;
    }

    public void setInteractiveValue(final String interactiveValue) {
        this.interactiveValue = interactiveValue;
    }

    public String getKey() {
        return key;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDescription() {
        return description;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public String getGroup() {
        return group;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getResolvedValue() {
        return resolvedValue;
    }

}
