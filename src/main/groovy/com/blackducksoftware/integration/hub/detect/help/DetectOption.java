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

import java.util.List;

import org.assertj.core.util.Arrays;

public class DetectOption {
    final String key;
    final String fieldName;
    final Class<?> valueType;
    final String originalValue;
    final String defaultValue;
    final String resolvedValue;
    final List<String> acceptableValues;
    String interactiveValue = null;

    final DetectOptionHelp help;
    
    public DetectOption(final String key, final String fieldName, final String originalValue, final String resolvedValue, final Class<?> valueType, final String defaultValue, final String[] acceptableValues, DetectOptionHelp help) {
        this.key = key;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.acceptableValues = Arrays.nonNullElementsIn(acceptableValues);
        this.fieldName = fieldName;
        this.originalValue = originalValue;
        this.resolvedValue = resolvedValue;
        
        this.help = help;
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

    public Class<?> getValueType() {
        return valueType;
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

    public DetectOptionHelp getHelp() {
        return help;
    }
    
    public List<String> getAcceptableValues() {
        return acceptableValues;
    }
    
}
