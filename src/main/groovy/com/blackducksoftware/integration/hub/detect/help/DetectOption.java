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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DetectOption {
    final String key;
    final String fieldName;
    final Class<?> valueType;
    final String originalValue;
    final String defaultValue;
    final String resolvedValue;
    final boolean strictAcceptableValues;
    final boolean caseSensitiveAcceptableValues;
    final List<String> acceptableValues;
    String interactiveValue = null;
    String finalValue = null;
    FinalValueType finalValueType = FinalValueType.DEFAULT;
    final DetectOptionHelp help;
    final FieldWarnings warnings;

    public enum FinalValueType{
        DEFAULT, //the final value is the value in the default attribute
        INTERACTIVE, //the final value is from the interactive prompt
        LATEST, //the final value was resolved from latest
        CALCULATED, //the resolved value was not set and final value was set during init
        SUPPLIED, //the final value most likely came from spring
        OVERRIDE //the resolved value was set but during init a new value was set
    }

    public DetectOption(final String key, final String fieldName, final String originalValue, final String resolvedValue, final Class<?> valueType, final String defaultValue, final boolean strictAcceptableValue, final boolean caseSensitiveAcceptableValues, final String[] acceptableValues, final DetectOptionHelp help, final FieldWarnings warnings) {
        this.key = key;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.acceptableValues = Arrays.stream(acceptableValues).collect(Collectors.toList());
        this.fieldName = fieldName;
        this.originalValue = originalValue;
        this.resolvedValue = resolvedValue;
        this.strictAcceptableValues = strictAcceptableValue;
        this.caseSensitiveAcceptableValues = caseSensitiveAcceptableValues;
        this.help = help;
        this.warnings = warnings;
    }

    public String getInteractiveValue() {
        return interactiveValue;
    }

    public void setInteractiveValue(final String interactiveValue) {
        this.interactiveValue = interactiveValue;
    }

    public String getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(final String finalValue) {
        this.finalValue = finalValue;
    }

    public void setFinalValue(final String finalValue, final FinalValueType finalValueType) {
        setFinalValue(finalValue);
        setFinalValueType(finalValueType);
    }

    public FinalValueType getFinalValueType() {
        return finalValueType;
    }

    public void setFinalValueType(final FinalValueType finalValueType) {
        this.finalValueType = finalValueType;
    }

    public String getKey() {
        return key;
    }

    public FieldWarnings getWarnings() {
        return warnings;
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

    public boolean getCaseSensistiveAcceptableValues() {
        return caseSensitiveAcceptableValues;
    }

    public boolean isAcceptableValue(final String value) {
        return acceptableValues.stream()
                .anyMatch(it -> {
                    if (caseSensitiveAcceptableValues) {
                        return it.equals(value);
                    } else {
                        return it.equalsIgnoreCase(value);
                    }
                });
    }

}
