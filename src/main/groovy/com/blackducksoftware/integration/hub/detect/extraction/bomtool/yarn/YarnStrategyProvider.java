package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class YarnStrategyProvider extends StrategyProvider {

    @Autowired
    public YarnLockStrategy yarnlockStrategy;

    @Override
    public void init() {

        add(yarnlockStrategy);

    }

}
