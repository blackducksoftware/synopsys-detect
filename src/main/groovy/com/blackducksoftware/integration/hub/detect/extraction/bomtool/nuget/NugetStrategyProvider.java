package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class NugetStrategyProvider extends StrategyProvider {

    @Autowired
    public NugetProjectStrategy projectStrategy;

    @Autowired
    public NugetSolutionStrategy solutionStrategy;

    @Override
    public void init() {

        add(solutionStrategy, projectStrategy);

    }

}
