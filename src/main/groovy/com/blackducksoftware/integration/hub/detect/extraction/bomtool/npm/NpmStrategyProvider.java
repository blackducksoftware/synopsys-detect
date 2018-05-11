package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn.YarnStrategyProvider;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;

@Component
public class NpmStrategyProvider extends StrategyProvider {

    @Autowired
    public NpmCliStrategy cliStrategy;

    @Autowired
    public NpmShrinkwrapStrategy shrinkwrapStrategy;

    @Autowired
    public NpmPackageLockStrategy packageLockStrategy;

    @Autowired
    public YarnStrategyProvider yarnStrategyProvider;

    @Override
    public void init() {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void lateInit() {

        for (final Strategy yarnStrategy : yarnStrategyProvider.getAllStrategies()) {
            packageLockStrategy.yieldsTo(yarnStrategy);
            shrinkwrapStrategy.yieldsTo(yarnStrategy);
            cliStrategy.yieldsTo(yarnStrategy);
        }

        cliStrategy.yieldsTo(shrinkwrapStrategy);
        cliStrategy.yieldsTo(packageLockStrategy);

        add(cliStrategy, packageLockStrategy, shrinkwrapStrategy);

    }
}
