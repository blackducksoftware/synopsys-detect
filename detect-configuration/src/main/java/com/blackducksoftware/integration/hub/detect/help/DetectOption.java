/**
 * detect-configuration
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlOption;
import com.blackducksoftware.integration.hub.detect.help.print.HelpTextWriter;

public abstract class DetectOption {
    private final DetectProperty detectProperty;
    private final List<String> validValues;
    private final boolean strictValidation;
    private final boolean caseSensitiveValidation;
    private final DetectOptionHelp detectOptionHelp;
    private final String resolvedValue;

    private final List<String> warnings = new ArrayList<>();

    private FinalValueType finalValueType = FinalValueType.DEFAULT;
    private String finalValue = null;
    private String interactiveValue = null;
    private String postInitValue = null;
    private boolean requestedDeprecation = false;

    public DetectOption(final DetectProperty detectProperty, final boolean strictValidation, final boolean caseSensitiveValidation, final List<String> validValues, final DetectOptionHelp detectOptionHelp, final String resolvedValue) {
        this.detectProperty = detectProperty;
        this.strictValidation = strictValidation;
        this.caseSensitiveValidation = caseSensitiveValidation;
        this.validValues = validValues;
        this.detectOptionHelp = detectOptionHelp;
        this.resolvedValue = resolvedValue;
    }

    public boolean isCommaSeperatedList() {
        return false;
    }

    public DetectProperty getDetectProperty() {
        return detectProperty;
    }

    public void requestDeprecation() {
        requestedDeprecation = true;
    }

    public void addWarning(final String description) {
        warnings.add(description);
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public boolean hasWarnings() {
        return warnings.size() > 0;
    }

    public boolean isRequestedDeprecation() {
        return requestedDeprecation;
    }

    public FinalValueType getFinalValueType() {
        return finalValueType;
    }

    public void setFinalValueType(final FinalValueType finalValueType) {
        this.finalValueType = finalValueType;
    }

    public DetectOptionHelp getDetectOptionHelp() {
        return detectOptionHelp;
    }

    public boolean hasStrictValidation() {
        return strictValidation;
    }

    public boolean hasCaseSensitiveValidation() {
        return caseSensitiveValidation;
    }

    public List<String> getValidValues() {
        return validValues;
    }

    public String getResolvedValue() {
        return resolvedValue;
    }

    public String getInteractiveValue() {
        return interactiveValue;
    }

    public void setInteractiveValue(final String interactiveValue) {
        this.interactiveValue = interactiveValue;
    }

    public String getPostInitValue() {
        return postInitValue;
    }

    public void setPostInitValue(final String postInitValue) {
        this.postInitValue = postInitValue;
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

    public abstract OptionValidationResult validateValue(final String value);

    public OptionValidationResult validate() {
        return validateValue(resolvedValue);
    }

    private String getDeprecationText() {
        return "Will cause failures in version " + getDetectOptionHelp().deprecationFailInVersion.getDisplayValue() + ". Will be removed in version " + getDetectOptionHelp().deprecationRemoveInVersion.getDisplayValue() + ". ";
    }

    public void printOption(final HelpTextWriter writer) {
        String description = getDetectOptionHelp().description;
        if (getDetectOptionHelp().isDeprecated) {
            description = getDeprecationText() + description;
        }
        if (getValidValues().size() > 0) {
            description += " (" + getValidValues().stream().collect(Collectors.joining("|")) + ")";
        }
        String propertyName = "";
        String defaultValue = "";
        if (StringUtils.isNotBlank(detectProperty.getPropertyName())) {
            propertyName = detectProperty.getPropertyName();
        }
        if (StringUtils.isNotBlank(detectProperty.getDefaultValue())) {
            defaultValue = detectProperty.getDefaultValue();
        }
        writer.printColumns("--" + propertyName, defaultValue, description);
    }

    public void printDetailedOption(final HelpTextWriter writer) {
        writer.println("");
        writer.println("Detailed information for " + detectProperty.getPropertyName());
        writer.println("");
        if (getDetectOptionHelp().isDeprecated) {
            writer.println("Deprecated: " + getDeprecationText());
            writer.println("Deprecation description: " + getDetectOptionHelp().deprecation);
            writer.println("");
        }
        writer.println("Property description: " + getDetectOptionHelp().description);
        writer.println("Property default value: " + detectProperty.getDefaultValue());
        if (getValidValues().size() > 0) {
            writer.println("Property acceptable values: " + getValidValues().stream().collect(Collectors.joining(", ")));
        }
        writer.println("");

        final DetectOptionHelp help = getDetectOptionHelp();
        if (StringUtils.isNotBlank(help.detailedHelp)) {
            writer.println("Detailed help:");
            writer.println(help.detailedHelp);
            writer.println();
        }
    }

    public HelpHtmlOption createHtmlOption() {
        final String description = getDetectOptionHelp().description;
        String acceptableValues = "";
        if (getValidValues().size() > 0) {
            acceptableValues = getValidValues().stream().collect(Collectors.joining(", "));
        }
        String deprecationNotice = "";
        if (getDetectOptionHelp().isDeprecated) {
            deprecationNotice = getDeprecationText() + getDetectOptionHelp().deprecation;
        }
        String propertyName = "";
        String defaultValue = "";
        if (StringUtils.isNotBlank(detectProperty.getPropertyName())) {
            propertyName = detectProperty.getPropertyName();
        }
        if (StringUtils.isNotBlank(detectProperty.getDefaultValue())) {
            defaultValue = detectProperty.getDefaultValue();
        }

        final HelpHtmlOption htmlOption = new HelpHtmlOption(propertyName, defaultValue, description, acceptableValues, getDetectOptionHelp().detailedHelp, deprecationNotice);
        return htmlOption;
    }

    public enum FinalValueType {
        DEFAULT, // the final value is the value in the default attribute
        INTERACTIVE, // the final value is from the interactive prompt
        LATEST, // the final value was resolved from latest
        CALCULATED, // the resolved value was not set and final value was set during init
        SUPPLIED, // the final value most likely came from spring
        OVERRIDE, // the resolved value was set but during init a new value was set
        COPIED // the resolved value was copied due to the setting of some other property, such as a deprecated property having an overide.
    }

    public static class OptionValidationResult {
        private final boolean isValid;
        private final String validationMessage;

        public static OptionValidationResult valid(final String message) {
            return new OptionValidationResult(true, message);
        }

        public static OptionValidationResult invalid(final String message) {
            return new OptionValidationResult(false, message);
        }

        private OptionValidationResult(final boolean isValid, final String validationMessage) {
            this.isValid = isValid;
            this.validationMessage = validationMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getValidationMessage() {
            return validationMessage;
        }
    }

    protected boolean validValuesContains(final String value) {
        if (hasCaseSensitiveValidation()) {
            return getValidValues().contains(value);
        }

        return getValidValues().stream().anyMatch(validValue -> validValue.equalsIgnoreCase(value));
    }

}
