package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class CondaStrategyProvider extends StrategyProvider {

    @Autowired
    protected CondaCliStrategy cliStrategy;

    @Override
    public void init() {

        add(cliStrategy );

    }
}
