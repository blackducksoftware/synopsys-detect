/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.help

import java.lang.reflect.Field

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.ReflectionUtils
import com.blackducksoftware.integration.hub.detect.util.SpringValueUtils

import groovy.transform.TypeChecked

@Component
@TypeChecked
public class DetectOptionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectOptionManager.class)

    @Autowired
    public DetectConfiguration detectConfiguration

    List<DetectOption> detectOptions
    public List<DetectOption> getDetectOptions() {
        detectOptions
    }

    public void init() {
        Map<String, DetectOption> detectOptionsMap = [:]

        DetectConfiguration.class.declaredFields.each { Field field ->
            DetectOption option = processField(detectConfiguration, DetectConfiguration.class, field)
            if (option != null) {
                if (!detectOptionsMap.containsKey(option.key)) {
                    detectOptionsMap.put(option.key, option)
                }
            }
        }

        detectOptions = detectOptionsMap.values().toSorted(new Comparator<DetectOption>() {
                    @Override
                    public int compare(DetectOption o1, DetectOption o2) {
                        if (o1.group.isEmpty()) {
                            return 1
                        } else if (o2.group.isEmpty()) {
                            return -1
                        } else {
                            return o1.group.compareTo(o2.group)
                        }
                    }
                })
    }

    private DetectOption processField(Object obj, Class<?> objClz, Field field) {
        if (field.isAnnotationPresent(ValueDescription.class)) {
            String fieldName = field.getName()
            String key = ''
            String description = ''
            Class valueType = field.getType()
            String defaultValue = ''
            String group = ''
            final ValueDescription valueDescription = field.getAnnotation(ValueDescription.class)
            description = valueDescription.description()
            defaultValue = valueDescription.defaultValue()
            group = valueDescription.group()
            if (field.isAnnotationPresent(Value.class)) {
                String valueKey = field.getAnnotation(Value.class).value().trim()
                key = SpringValueUtils.springKeyFromValueAnnotation(valueKey)
            }

            field.setAccessible(true)
            boolean hasValue = !ReflectionUtils.isValueNull(field, obj)

            String originalValue = defaultValue
            String finalValue = originalValue

            if (defaultValue?.trim() && !hasValue) {
                try {
                    finalValue = defaultValue
                    ReflectionUtils.setValue(field, obj, defaultValue)
                } catch (final IllegalAccessException e) {
                    logger.error(String.format("Could not set defaultValue on field %s with %s: %s", field.getName(), defaultValue, e.getMessage()))
                }
            } else if (hasValue) {
                finalValue = field.get(obj).toString()
            }

            return new DetectOption(key, fieldName, originalValue, finalValue, description, valueType, defaultValue, group)
        }
        return null
    }
}
