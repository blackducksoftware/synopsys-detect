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

    public List<PackmanOption> getPackmanValues() {
        Map<String, PackmanOption> packmanOptions = [:]
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
                    if (!packmanOptions.containsKey(key)) {
                        packmanOptions.put(key, new PackmanOption(key, description))
                    }
                }
            }
        }

        packmanOptions.values().toSorted { a, b ->
            a.key <=> b.key
        }
    }
}
