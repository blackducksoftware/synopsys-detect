package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class PipStrategyProvider extends StrategyProvider {

    @Autowired
    public PipInspectorStrategy pipStrategy;

    @Override
    public void init() {

        add(pipStrategy);

    }

}
