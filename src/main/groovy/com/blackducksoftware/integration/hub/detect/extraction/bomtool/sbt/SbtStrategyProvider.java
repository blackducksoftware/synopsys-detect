package com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class SbtStrategyProvider extends StrategyProvider {

    @Autowired
    public SbtResolutionCacheStrategy buildSbtStrategy;

    @Override
    public void init() {

        add(buildSbtStrategy);

    }

}
