package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class CranStrategyProvider  extends StrategyProvider {

    @Autowired
    protected PackratLockStrategy packratStrategy;

    @Override
    public void init() {

        add(packratStrategy);

    }

}
