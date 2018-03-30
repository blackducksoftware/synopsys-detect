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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveOption;
import com.blackducksoftware.integration.hub.detect.util.SpringValueUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

@Component
public class DetectOptionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectOptionManager.class);

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public Gson gson;

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
        detectGroups = new ArrayList<>();
        Map<String, DetailedDetectOption> optionDetails = new HashMap<>();

        try {
            final Type detailedDetectOptionType = new TypeToken<HashMap<String, DetailedDetectOption>>() {
            }.getType();
            final String detectDetailedOptions = getClass().getResource("/detect-options.json").getFile();
            final FileReader fileReader = new FileReader(detectDetailedOptions);
            final JsonReader jsonReader = new JsonReader(fileReader);
            optionDetails = gson.fromJson(jsonReader, detailedDetectOptionType);
        } catch (final FileNotFoundException e) {
            logger.warn("detect-options.json file not found. Can't use detailed properties help option.");
        }

        for (final Field field : DetectConfiguration.class.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(ValueDescription.class)) {
                    final DetectOption option = processField(detectConfiguration, DetectConfiguration.class, field, optionDetails);
                    if (option != null) {
                        if (!detectOptionsMap.containsKey(option.key)) {
                            detectOptionsMap.put(option.key, option);
                        }
                    }
                } else if (field.getName().startsWith("GROUP_")) {
                    field.setAccessible(true);
                    detectGroups.add(field.get(null).toString());
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error(String.format("Could not resolve field %s: %s", field.getName(), e.getMessage()));
            }
        }

        Collections.sort(detectGroups);
        detectOptions = detectOptionsMap.values().stream().sorted(new Comparator<DetectOption>() {
            @Override
            public int compare(final DetectOption o1, final DetectOption o2) {
                if (o1.group.isEmpty()) {
                    return 1;
                } else if (o2.group.isEmpty()) {
                    return -1;
                } else {
                    return o1.group.compareTo(o2.group);
                }
            }
        }).collect(Collectors.toList());
    }

    private DetectOption processField(final Object obj, final Class<?> objClz, final Field field, final Map<String, DetailedDetectOption> detailedDetectOptionsMap) throws IllegalArgumentException, IllegalAccessException {
        final String fieldName = field.getName();
        String key = "";
        String description = "";
        final Class<?> valueType = field.getType();
        String defaultValue = "";
        String group = "";
        final ValueDescription valueDescription = field.getAnnotation(ValueDescription.class);
        description = valueDescription.description();
        defaultValue = valueDescription.defaultValue();
        group = valueDescription.group();
        if (field.isAnnotationPresent(Value.class)) {
            final String valueKey = field.getAnnotation(Value.class).value().trim();
            key = SpringValueUtils.springKeyFromValueAnnotation(valueKey);
        }

        field.setAccessible(true);
        final boolean hasValue = !isValueNull(field, obj);

        final String originalValue = defaultValue;
        String resolvedValue = originalValue;

        if (defaultValue != null && !defaultValue.trim().isEmpty() && !hasValue) {
            resolvedValue = defaultValue;
            setValue(field, obj, defaultValue);
        } else if (hasValue) {
            resolvedValue = field.get(obj).toString();
        }

        final DetectOption detectOption = new DetectOption(key, fieldName, originalValue, resolvedValue, description, valueType, defaultValue, group);
        final DetailedDetectOption detailedDetectOption = detailedDetectOptionsMap.get(key);

        if (detailedDetectOption != null) {
            detectOption.setDetailedDetectOption(detailedDetectOption);
        }

        return detectOption;
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

    public void setValue(final Field field, final Object obj, final String value) {
        final Class<?> type = field.getType();
        try {
            if (String.class == type) {
                field.set(obj, value);
            } else if (Integer.class == type) {
                field.set(obj, NumberUtils.toInt(value));
            } else if (Long.class == type) {
                field.set(obj, NumberUtils.toLong(value));
            } else if (Boolean.class == type) {
                field.set(obj, Boolean.parseBoolean(value));
            }
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
        if ((String.class == type && fieldValue.toString().trim().length() == 0) || (Integer.class == type && fieldValue == null) || (Long.class == type && fieldValue == null) || (Boolean.class == type && fieldValue == null)) {
            return true;
        }
        return false;
    }
}
