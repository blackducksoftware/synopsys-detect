/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
