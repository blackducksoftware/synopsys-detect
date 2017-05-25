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
package com.blackducksoftware.integration.hub.packman.help;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AnnotationFinder implements ApplicationContextAware {
    private final List<ValueDescription> valueDescriptions = new ArrayList<>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<ValueDescription> getValueDescriptions() {
        // TODO if value is present use that key
        for (final String beanName : applicationContext.getBeanDefinitionNames()) {
            final Object obj = applicationContext.getBean(beanName);
            Class<?> objClz = obj.getClass();
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }
            for (final Field f : objClz.getDeclaredFields()) {
                if (f.isAnnotationPresent(ValueDescription.class)) {
                    valueDescriptions.add(f.getAnnotation(ValueDescription.class));
                }
                if (f.isAnnotationPresent(Value.class)) {
                    System.out.println(f.getAnnotation(Value.class).value());
                }
            }
        }
        return valueDescriptions;
    }

}
