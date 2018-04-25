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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.help.DetectOption.FinalValueType;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveOption;
import com.blackducksoftware.integration.hub.detect.util.SpringValueUtils;

@Component
public class DetectOptionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectOptionManager.class);

    @Autowired
    public DetectConfiguration detectConfiguration;

    private List<DetectOption> detectOptions;
    private List<String> detectGroups;

    public List<DetectOption> getDetectOptions() {
        return detectOptions;
    }

    public List<String> getDetectGroups() {
        return detectGroups;
    }

    public void init() {
        final Map<String, DetectOption> detectOptionsMap = new HashMap<>();

        for (final Field field : DetectConfiguration.class.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(Value.class)) {
                    final DetectOption option = processField(detectConfiguration, DetectConfiguration.class, field);
                    if (option != null) {
                        if (!detectOptionsMap.containsKey(option.key)) {
                            detectOptionsMap.put(option.key, option);
                        }
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error(String.format("Could not resolve field %s: %s", field.getName(), e.getMessage()));
            }
        }

        detectOptions = detectOptionsMap.values().stream()
                                .sorted((o1, o2) -> o1.getHelp().primaryGroup.compareTo(o2.getHelp().primaryGroup))
                                .collect(Collectors.toList());

        detectGroups = detectOptions.stream()
                               .map(it -> it.getHelp().primaryGroup)
                               .distinct()
                               .sorted()
                               .collect(Collectors.toList());
    }

    public void postInit() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, DetectUserFriendlyException {
        for (final DetectOption option : detectOptions) {
            final String fieldValue = getCurrentValue(detectConfiguration, option);
            if (!option.getResolvedValue().equals(fieldValue)) {
                if (option.getInteractiveValue() != null) {
                    option.setFinalValue(fieldValue, FinalValueType.INTERACTIVE);
                } else if (option.getResolvedValue().equals("latest")) {
                    option.setFinalValue(fieldValue, FinalValueType.LATEST);
                } else if (option.getResolvedValue().trim().length() == 0) {
                    option.setFinalValue(fieldValue, FinalValueType.CALCULATED);
                } else {
                    option.setFinalValue(fieldValue, FinalValueType.OVERRIDE);
                }
            } else {
                if (fieldValue.equals(option.getDefaultValue())) {
                    option.setFinalValue(fieldValue, FinalValueType.DEFAULT);
                } else {
                    option.setFinalValue(fieldValue, FinalValueType.SUPPLIED);
                    if (option.getHelp().isDeprecated) {
                        option.requestDeprecation();
                    }
                }
            }

            if (option.isRequestedDeprecation()) {
                option.addWarning("As of version " + option.getHelp().deprecationVersion + " this property will be removed: " + option.getHelp().deprecation);
            }
        }
    }

    public String getCurrentValue(final DetectConfiguration detectConfiguration, final DetectOption detectOption) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        final Field field = detectConfiguration.getClass().getDeclaredField(detectOption.getFieldName());
        field.setAccessible(true);
        String fieldValue = getStringValue(detectConfiguration, field);
        field.setAccessible(false);
        return fieldValue;
    }

    private String getStringValue(Object obj, Field field) throws IllegalAccessException {
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

    private DetectOption processField(final Object obj, final Class<?> objClz, final Field field) throws IllegalArgumentException, IllegalAccessException {
        final String fieldName = field.getName();
        final Class<?> valueType = field.getType();

        final Value valueAnnotation = field.getAnnotation(Value.class);
        final String key = SpringValueUtils.springKeyFromValueAnnotation(valueAnnotation.value());

        String defaultValue = "";
        final DefaultValue defaultValueAnnotation = field.getAnnotation(DefaultValue.class);
        if (defaultValueAnnotation != null) {
            defaultValue = defaultValueAnnotation.value();
        }

        String[] acceptableValues = new String[] {};
        boolean strictAcceptableValue = false;
        boolean caseSensitiveAcceptableValues = false;
        final AcceptableValues acceptableValueAnnotation = field.getAnnotation(AcceptableValues.class);
        if (acceptableValueAnnotation != null) {
            acceptableValues = acceptableValueAnnotation.value();
            strictAcceptableValue = acceptableValueAnnotation.strict();
            caseSensitiveAcceptableValues = acceptableValueAnnotation.caseSensitive();
        }

        final String originalValue = defaultValue;
        String resolvedValue = originalValue;
        field.setAccessible(true);

        final boolean hasValue = !isValueNull(field, obj);
        if (defaultValue != null && !defaultValue.trim().isEmpty() && !hasValue) {
            resolvedValue = defaultValue;
            setValue(field, obj, defaultValue);
        } else if (hasValue) {
            resolvedValue = getStringValue(obj, field);
        }

        final DetectOptionHelp help = processFieldHelp(field);

        return new DetectOption(key, fieldName, originalValue, resolvedValue, valueType, defaultValue, strictAcceptableValue, caseSensitiveAcceptableValues, acceptableValues, help);
    }

    private DetectOptionHelp processFieldHelp(final Field field) {
        final DetectOptionHelp help = new DetectOptionHelp();

        final HelpDescription descriptionAnnotation = field.getAnnotation(HelpDescription.class);
        help.description = descriptionAnnotation.value();

        final HelpGroup groupAnnotation = field.getAnnotation(HelpGroup.class);
        final String primaryGroup = groupAnnotation.primary();
        final String[] additionalGroups = groupAnnotation.additional();
        if (additionalGroups.length > 0) {
            help.groups.addAll(Arrays.stream(additionalGroups).collect(Collectors.toList()));
        } else {
            if (StringUtils.isNotBlank(primaryGroup)) {
                help.groups.add(primaryGroup);
            }
        }

        final HelpUseCases useCasesAnnotation = field.getAnnotation(HelpUseCases.class);
        if (useCasesAnnotation != null) {
            help.useCases = useCasesAnnotation.value();
        }

        final HelpIssues issuesAnnotation = field.getAnnotation(HelpIssues.class);
        if (issuesAnnotation != null) {
            help.issues = issuesAnnotation.value();
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
            if (option.strictAcceptableValues) {
                if (!option.isAcceptableValue(option.resolvedValue)) {
                    unacceptableDetectOptions.add(option);
                }
            }
        }
        return unacceptableDetectOptions;
    }

    public void applyInteractiveOptions(final List<InteractiveOption> interactiveOptions) {
        for (final InteractiveOption interactiveOption : interactiveOptions) {
            for (final DetectOption detectOption : detectOptions) {
                if (detectOption.getFieldName().equals(interactiveOption.getFieldName())) {
                    detectOption.interactiveValue = interactiveOption.getInteractiveValue();
                }
            }

            final Field field;
            try {
                field = detectConfiguration.getClass().getDeclaredField(interactiveOption.getFieldName());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            setValue(field, detectConfiguration, interactiveOption.getInteractiveValue());
        }
    }

    public Object setValue(final Field field, final Object obj, final String value) {
        final Class<?> type = field.getType();
        try {
            Object objectValue = null;
            if (String.class == type) {
                objectValue = value;
            } else if (Integer.class == type) {
                objectValue = NumberUtils.toInt(value);
            } else if (Long.class == type) {
                objectValue = NumberUtils.toLong(value);
            } else if (Boolean.class == type) {
                objectValue = Boolean.parseBoolean(value);
            } else if (String[].class == type) {
                objectValue = value.split(",");
            }
            field.set(obj, objectValue);
            return objectValue;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValueNull(final Field field, final Object obj) {
        final Class<?> type = field.getType();
        final Object fieldValue;
        try {
            fieldValue = field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (String.class == type && fieldValue.toString().trim().length() == 0) {
            return true;
        } else if (Integer.class == type && fieldValue == null) {
            return true;
        } else if (Long.class == type && fieldValue == null) {
            return true;
        } else if (Boolean.class == type && fieldValue == null) {
            return true;
        } else if (String[].class == type) {
            if (fieldValue == null) {
                return true;
            }
            final String[] realValue = (String[]) fieldValue;
            return realValue.length <= 0;
        } else {
            return false;
        }

    }
}
