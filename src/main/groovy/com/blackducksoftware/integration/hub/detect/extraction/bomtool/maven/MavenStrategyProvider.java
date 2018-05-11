package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class MavenStrategyProvider extends StrategyProvider {

    @Autowired
    public MavenPomStrategy pomStrategy;

    @Autowired
    public MavenPomWrapperStrategy pomWrapperStrategy;

    @Override
    public void init() {

        add(pomStrategy, pomWrapperStrategy);

    }

}
