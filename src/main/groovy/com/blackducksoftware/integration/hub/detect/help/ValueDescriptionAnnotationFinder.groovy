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

import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

import groovy.transform.TypeChecked

@Component
@TypeChecked
public class ValueDescriptionAnnotationFinder implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(ValueDescriptionAnnotationFinder.class)

    private ApplicationContext applicationContext

    List<DetectOption> detectOptions

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    public void init() {
        Map<String, DetectOption> detectOptionsMap = [:]
        applicationContext.beanDefinitionNames.each { String beanName ->
            final Object obj = applicationContext.getBean(beanName)
            Class<?> objClz = obj.getClass()
            if (AopUtils.isAopProxy(obj)) {
                objClz = AopUtils.getTargetClass(obj)
            }
            objClz.declaredFields.each { Field field ->
                if (field.isAnnotationPresent(ValueDescription.class)) {
                    String key = ''
                    String description = ''
                    Class valueType = field.getType()
                    String defaultValue = ''
                    String group = ''
                    final ValueDescription valueDescription = field.getAnnotation(ValueDescription.class)
                    description = valueDescription.description()
                    defaultValue = valueDescription.defaultValue()
                    group = valueDescription.group()
                    if (!valueDescription.key()?.trim()) {
                        if (field.isAnnotationPresent(Value.class)) {
                            String valueKey = field.getAnnotation(Value.class).value().trim()
                            key = valueKey[2..-2]
                        }
                    } else {
                        key = valueDescription.key().trim()
                    }
                    field.setAccessible(true)
                    Object fieldValue = field.get(obj)
                    if (defaultValue?.trim()) {
                        try {
                            Class type = field.getType()
                            if (String.class == type && fieldValue == null) {
                                field.set(obj, defaultValue)
                            } else if (Integer.class == type && fieldValue == null) {
                                field.set(obj, NumberUtils.toInt(defaultValue))
                            } else if (Long.class == type && fieldValue == null) {
                                field.set(obj, NumberUtils.toLong(defaultValue))
                            } else if (Boolean.class == type && fieldValue == null) {
                                field.set(obj, Boolean.parseBoolean(defaultValue))
                            }
                        } catch (final IllegalAccessException e) {
                            logger.error(String.format("Could not set defaultValue on field %s with %s: %s", field.getName(), defaultValue, e.getMessage()))
                        }
                    }
                    if (!detectOptionsMap.containsKey(key)) {
                        detectOptionsMap.put(key, new DetectOption(key, description, valueType, defaultValue, group))
                    }
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

    public List<DetectOption> getDetectValues() {
        detectOptions
    }
}
