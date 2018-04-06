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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
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

        detectOptions = detectOptionsMap.values().stream().sorted(new Comparator<DetectOption>() {
            @Override
            public int compare(final DetectOption o1, final DetectOption o2) {
                if (o1.help.primaryGroup.isEmpty()) {
                    return 1;
                } else if (o2.help.primaryGroup.isEmpty()) {
                    return -1;
                } else {
                    return o1.help.primaryGroup.compareTo(o2.help.primaryGroup);
                }
            }
        }).collect(Collectors.toList());
        
        detectGroups = detectOptions.stream()
                .map(it -> it.help.primaryGroup)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private DetectOption processField(final Object obj, final Class<?> objClz, final Field field) throws IllegalArgumentException, IllegalAccessException {
        final String fieldName = field.getName();
        final Class<?> valueType = field.getType();

        final Value valueAnnotation = field.getAnnotation(Value.class);
        String key = SpringValueUtils.springKeyFromValueAnnotation(valueAnnotation.value());
        
        String defaultValue = "";
        DefaultValue defaultValueAnnotation = field.getAnnotation(DefaultValue.class);
        if (defaultValueAnnotation != null) {
            defaultValue = defaultValueAnnotation.value();
        }
        
        String[] acceptableValues = new String[] {};
        AcceptableValues acceptableValueAnnotation = field.getAnnotation(AcceptableValues.class);
        if (acceptableValueAnnotation != null) {
            acceptableValues = acceptableValueAnnotation.value();
        }
        
        final String originalValue = defaultValue;
        String resolvedValue = originalValue;
        field.setAccessible(true);

        boolean hasValue = !isValueNull(field, obj);
        if (defaultValue != null && !defaultValue.trim().isEmpty() && !hasValue) {
            resolvedValue = defaultValue;
            setValue(field, obj, defaultValue);
        } else if (hasValue) {
            resolvedValue = field.get(obj).toString();
        }
        
        DetectOptionHelp help = helpFromField(field);

        return new DetectOption(key, fieldName, originalValue, resolvedValue, valueType, defaultValue, acceptableValues, help);
    }
    
    public DetectOptionHelp helpFromField(final Field field) {
        DetectOptionHelp help = new DetectOptionHelp();
        
        final HelpDescription descriptionAnnotation = field.getAnnotation(HelpDescription.class);
        help.description = descriptionAnnotation.value();
        
        final HelpGroup groupAnnotation = field.getAnnotation(HelpGroup.class);
        String primaryGroup = groupAnnotation.primary();
        String[] additionalGroups = groupAnnotation.additional();
        if (additionalGroups.length > 0) {
            help.groups.addAll(Arrays.nonNullElementsIn(additionalGroups));
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
            }else if (String[].class == type) {
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
            String[] realValue = (String[]) fieldValue;
            return realValue.length <= 0;
        } else {
            return false;
        }
        
    }
}
