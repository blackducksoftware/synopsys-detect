package com.blackducksoftware.integration.hub.detect.lifecycle;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;

public class DetectContext {

    AnnotationConfigApplicationContext springContext;
    ConfigurableListableBeanFactory beanFactory;

    private boolean lock = false;

    public DetectContext(DetectRun detectRun) {
        //Detect context is currently actually backed by Spring.
        springContext = new AnnotationConfigApplicationContext();
        springContext.setDisplayName("Detect Context " + detectRun.getRunId());
        beanFactory = springContext.getBeanFactory();
    }

    public void registerConfiguration(Class configuration) {
        checkLock();
        springContext.register(configuration);

    }

    public <T> T registerBean(T singleton) {
        checkLock();
        //register and return the registered object as a convenience
        beanFactory.registerSingleton(singleton.getClass().getSimpleName(), singleton);
        return singleton;
    }

    public <T> T getBean(Class<T> beanClass) {
        return beanFactory.getBean(beanClass);
    }

    public <T> T getBean(Class<T> beanClass, Object... args) {
        return beanFactory.getBean(beanClass, args);
    }

    public void lock() {
        springContext.refresh(); //can only trigger once.
        this.lock = true;
    }

    private void checkLock() {
        if (this.lock) {
            throw new RuntimeException("Cannot change context!");
        }
    }

}
