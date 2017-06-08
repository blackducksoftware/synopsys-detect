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

import org.springframework.aop.support.AopUtils
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
public class ValueDescriptionAnnotationFinder implements ApplicationContextAware {
    private ApplicationContext applicationContext

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    public List<DetectOption> getDetectValues() {
        Map<String, DetectOption> detectOptions = [:]
        applicationContext.beanDefinitionNames.each { beanName ->
            final Object obj = applicationContext.getBean(beanName)
            Class<?> objClz = obj.getClass()
            if (AopUtils.isAopProxy(obj)) {
                objClz = AopUtils.getTargetClass(obj)
            }
            objClz.declaredFields.each { field ->
                if (field.isAnnotationPresent(ValueDescription.class)) {
                    String key = ''
                    String description = ''
                    final ValueDescription valueDescription = field.getAnnotation(ValueDescription.class)
                    description = valueDescription.description()
                    if (!valueDescription.key()?.trim()) {
                        if (field.isAnnotationPresent(Value.class)) {
                            String valueKey = field.getAnnotation(Value.class).value().trim()
                            key = valueKey[2..-2]
                        }
                    } else{
                        key = valueDescription.key().trim()
                    }
                    if (!detectOptions.containsKey(key)) {
                        detectOptions.put(key, new DetectOption(key, description))
                    }
                }
            }
        }

        detectOptions.values().toSorted { a, b ->
            a.key <=> b.key
        }
    }
}
