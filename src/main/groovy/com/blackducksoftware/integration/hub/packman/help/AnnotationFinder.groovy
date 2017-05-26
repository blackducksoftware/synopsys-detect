/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.help

import java.lang.reflect.Field

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
public class AnnotationFinder implements ApplicationContextAware {
    private final Set<ValueDescription> valueDescriptions = new HashSet<>()

    private ApplicationContext applicationContext

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    public List<ValueDescription> getValueDescriptions() {
        for (final String beanName : applicationContext.getBeanDefinitionNames()) {
            final Object obj = applicationContext.getBean(beanName)
            Class<?> objClz = obj.getClass()
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj)
            }
            for (final Field field : objClz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ValueDescription.class)) {
                    final ValueDescription valueDescription = field.getAnnotation(ValueDescription.class)
                    if (StringUtils.isBlank(valueDescription.key())) {
                        if (field.isAnnotationPresent(Value.class)) {
                            String key = field.getAnnotation(Value.class).value().trim()
                            key = key.substring(2, key.length() - 1)
                            valueDescription.key = key
                        }
                    }
                    valueDescriptions.add(valueDescription)
                }
            }
        }
        valueDescriptions
    }
}
