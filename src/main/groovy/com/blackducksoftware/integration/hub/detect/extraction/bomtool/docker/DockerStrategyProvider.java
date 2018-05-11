package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class DockerStrategyProvider extends StrategyProvider {

    @Autowired
    protected DockerStrategy dockerStrategy;

    @Override
    public void init() {

        add(dockerStrategy);

    }

}
