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
package com.blackducksoftware.integration.hub.detect.help;

import java.util.HashMap;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AnnotationManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<Object, Class<?>> findBeanClasses() {
        final Map<Object, Class<?>> clazzes = new HashMap<>();
        for (final String beanName : applicationContext.getBeanDefinitionNames()) {
            final Object obj = applicationContext.getBean(beanName);
            Class<?> objClz = obj.getClass();
            if (AopUtils.isAopProxy(obj)) {
                objClz = AopUtils.getTargetClass(obj);
            }
            clazzes.put(obj, objClz);
        }
        return clazzes;
    }

}
