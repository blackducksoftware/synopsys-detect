package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class CocoapodsStrategyProvider extends StrategyProvider {

    @Autowired
    public PodlockStrategy podlockStrategy;

    @Override
    public void init() {

        add(podlockStrategy);

    }

}