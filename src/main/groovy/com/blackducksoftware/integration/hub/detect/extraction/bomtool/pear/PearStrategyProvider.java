package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class PearStrategyProvider extends StrategyProvider {

    @Autowired
    public PearCliStrategy cliStrategy;

    @Override
    public void init() {

        add(cliStrategy);

    }

}
