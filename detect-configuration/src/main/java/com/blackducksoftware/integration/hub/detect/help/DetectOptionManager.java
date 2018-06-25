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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveOption;

public class DetectOptionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectOptionManager.class);

    public final DetectConfigWrapper detectConfigWrapper;

    private List<DetectOption> detectOptions;
    private List<String> detectGroups;

    public DetectOptionManager(final DetectConfigWrapper detectConfigWrapper) {
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public List<DetectOption> getDetectOptions() {
        return detectOptions;
    }

    public List<String> getDetectGroups() {
        return detectGroups;
    }

    public void init() {
        final Map<DetectProperty, DetectOption> detectOptionsMap = new HashMap<>();

        Map<DetectProperty, Object> propertyMap = detectConfigWrapper.getPropertyMap();
        if (null != propertyMap && !propertyMap.isEmpty()) {
            for (Map.Entry<DetectProperty, Object> propertyEntry : propertyMap.entrySet()) {
                final DetectOption option = processField(propertyEntry.getKey(), propertyMap, detectConfigWrapper);
                if (option != null) {
                    if (!detectOptionsMap.containsKey(propertyEntry.getKey())) {
                        detectOptionsMap.put(propertyEntry.getKey(), option);
                    }
                }
            }
        }

        detectOptions = detectOptionsMap.values().stream()
                .sorted((o1, o2) -> o1.getDetectOptionHelp().primaryGroup.compareTo(o2.getDetectOptionHelp().primaryGroup))
                .collect(Collectors.toList());

        detectGroups = detectOptions.stream()
                .map(it -> it.getDetectOptionHelp().primaryGroup)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void postInit() throws IllegalArgumentException, SecurityException {
        Map<DetectProperty, Object> propertyMap = detectConfigWrapper.getPropertyMap();
        for (final DetectOption option : detectOptions) {
            String fieldValue = option.getPostInitValue();
            if (StringUtils.isBlank(fieldValue)) {
                Object object = propertyMap.get(option.getDetectProperty());
                fieldValue = object.toString();
            }
            if (!option.getResolvedValue().equals(fieldValue)) {
                if (option.getInteractiveValue() != null) {
                    option.setFinalValue(fieldValue, DetectOption.FinalValueType.INTERACTIVE);
                } else if (option.getResolvedValue().equals("latest")) {
                    option.setFinalValue(fieldValue, DetectOption.FinalValueType.LATEST);
                } else if (option.getResolvedValue().trim().length() == 0) {
                    option.setFinalValue(fieldValue, DetectOption.FinalValueType.CALCULATED);
                } else {
                    option.setFinalValue(fieldValue, DetectOption.FinalValueType.OVERRIDE);
                }
            } else {
                if (fieldValue.equals(option.getDetectProperty().getDefaultValue())) {
                    option.setFinalValue(fieldValue, DetectOption.FinalValueType.DEFAULT);
                } else {
                    option.setFinalValue(fieldValue, DetectOption.FinalValueType.SUPPLIED);
                    if (option.getDetectOptionHelp().isDeprecated) {
                        option.requestDeprecation();
                    }
                }
            }

            if (option.isRequestedDeprecation()) {
                option.addWarning("As of version " + option.getDetectOptionHelp().deprecationVersion + " this property will be removed: " + option.getDetectOptionHelp().deprecation);
            }
        }
    }

    private String getStringValue(final Object obj, final Field field) throws IllegalAccessException {
        final Object rawFieldValue = field.get(obj);
        String fieldValue = "";
        if (field.getType().isArray()) {
            fieldValue = String.join(", ", (String[]) rawFieldValue);
        } else {
            if (rawFieldValue != null) {
                fieldValue = rawFieldValue.toString();
            }
        }
        return fieldValue;
    }

    private DetectOption processField(final DetectProperty detectProperty, Map<DetectProperty, Object> propertyMap, DetectConfigWrapper detectConfigWrapper) {
        try {
            Field field = DetectProperty.class.getField(detectProperty.name());

            String defaultValue = detectProperty.getDefaultValue();

            List<String> acceptableValues = new ArrayList<>();
            boolean isCommaSeparatedList = false;
            boolean strictAcceptableValue = false;
            boolean caseSensitiveAcceptableValues = false;
            final AcceptableValues acceptableValueAnnotation = field.getAnnotation(AcceptableValues.class);
            if (acceptableValueAnnotation != null) {
                acceptableValues = Arrays.asList(acceptableValueAnnotation.value());
                strictAcceptableValue = acceptableValueAnnotation.strict();
                caseSensitiveAcceptableValues = acceptableValueAnnotation.caseSensitive();
                isCommaSeparatedList = acceptableValueAnnotation.isCommaSeparatedList();
            }

            String resolvedValue = defaultValue;
            field.setAccessible(true);

            Object currentValue = propertyMap.get(detectProperty);

            final boolean hasValue = null != currentValue;
            if (defaultValue != null && !defaultValue.trim().isEmpty() && !hasValue) {
                resolvedValue = defaultValue;
                detectConfigWrapper.setDetectProperty(detectProperty, resolvedValue);
            } else if (hasValue) {
                resolvedValue = currentValue.toString();
            }

            final DetectOptionHelp help = processFieldHelp(field);

            DetectOption detectOption;
            if (isCommaSeparatedList) {
                detectOption = new DetectListOption(detectProperty, strictAcceptableValue, caseSensitiveAcceptableValues, acceptableValues, help, resolvedValue);
            } else {
                detectOption = new DetectSingleOption(detectProperty, strictAcceptableValue, caseSensitiveAcceptableValues, acceptableValues, help, resolvedValue);
            }

            return detectOption;
        } catch (IllegalArgumentException | NoSuchFieldException e) {
            logger.error(String.format("Could not resolve field %s: %s", detectProperty.name(), e.getMessage()));
        }
        return null;
    }

    private DetectOptionHelp processFieldHelp(final Field field) {
        final DetectOptionHelp help = new DetectOptionHelp();

        final HelpDescription descriptionAnnotation = field.getAnnotation(HelpDescription.class);
        help.description = descriptionAnnotation.value();

        final HelpGroup groupAnnotation = field.getAnnotation(HelpGroup.class);
        help.primaryGroup = groupAnnotation.primary();
        final String[] additionalGroups = groupAnnotation.additional();
        if (additionalGroups.length > 0) {
            help.groups.addAll(Arrays.stream(additionalGroups).collect(Collectors.toList()));
        } else {
            if (StringUtils.isNotBlank(help.primaryGroup)) {
                help.groups.add(help.primaryGroup);
            }
        }

        final HelpDetailed issuesAnnotation = field.getAnnotation(HelpDetailed.class);
        if (issuesAnnotation != null) {
            help.detailedHelp = issuesAnnotation.value();
        }

        final ValueDeprecation deprecationAnnotation = field.getAnnotation(ValueDeprecation.class);
        if (deprecationAnnotation != null) {
            help.isDeprecated = true;
            help.deprecation = deprecationAnnotation.description();
            help.deprecationVersion = deprecationAnnotation.willRemoveInVersion();
        }

        return help;
    }

    public List<DetectOption> findUnacceptableValues() throws DetectUserFriendlyException {
        final List<DetectOption> unacceptableDetectOptions = new ArrayList<>();
        for (final DetectOption option : detectOptions) {
            if (option.isStrictAcceptableValues()) {
                DetectOption.OptionValidationResult validationResult = option.isAcceptableValue(option.getResolvedValue());
                if (!validationResult.isValid()) {
                    unacceptableDetectOptions.add(option);
                }
            }
        }
        return unacceptableDetectOptions;
    }

    public void applyInteractiveOptions(final List<InteractiveOption> interactiveOptions) {
        for (final InteractiveOption interactiveOption : interactiveOptions) {
            for (final DetectOption detectOption : detectOptions) {
                if (detectOption.getDetectProperty().equals(interactiveOption.getDetectProperty())) {
                    detectOption.setInteractiveValue(interactiveOption.getInteractiveValue());
                    detectConfigWrapper.setDetectProperty(detectOption.getDetectProperty(), interactiveOption.getInteractiveValue());
                    break;
                }
            }
        }
    }

}
