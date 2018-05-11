package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class GradleStrategyProvider extends StrategyProvider {

    @Autowired
    public GradleInspectorStrategy gradleStrategy;


    @Override
    public void init() {

        add(gradleStrategy);

    }

}
