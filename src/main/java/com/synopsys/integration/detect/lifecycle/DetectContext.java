/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.synopsys.integration.detect.workflow.DetectRun;

public class DetectContext {
    private final AnnotationConfigApplicationContext springContext;
    private final ConfigurableListableBeanFactory beanFactory;

    private boolean lock = false;

    public DetectContext(final DetectRun detectRun) {
        //Detect context is currently actually backed by Spring.
        springContext = new AnnotationConfigApplicationContext();
        springContext.setDisplayName("Detect Context " + detectRun.getRunId());
        beanFactory = springContext.getBeanFactory();
    }

    public void registerConfiguration(final Class configuration) {
        checkLock();
        springContext.register(configuration);

    }

    public <T> T registerBean(final T singleton) {
        checkLock();
        //register and return the registered object as a convenience
        beanFactory.registerSingleton(singleton.getClass().getSimpleName(), singleton);
        return singleton;
    }

    public <T> T getBean(final Class<T> beanClass) {
        return beanFactory.getBean(beanClass);
    }

    public <T> T getBean(final Class<T> beanClass, final Object... args) {
        return beanFactory.getBean(beanClass, args);
    }

    public void lock() {
        springContext.refresh(); //can only trigger once.
        this.lock = true;
    }

    private void checkLock() {
        if (this.lock) {
            throw new RuntimeException("Cannot change detect context, it has been finalized!");
        }
    }

}
