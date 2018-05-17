package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyCoordinator;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class NpmStrategyCoordinator extends StrategyCoordinator {

    @Autowired
    public NpmCliStrategy cliStrategy;

    @Autowired
    public NpmShrinkwrapStrategy shrinkwrapStrategy;

    @Autowired
    public NpmPackageLockStrategy packageLockStrategy;

    @Autowired
    public List<Strategy> strategies;

    @Override
    public void init() {

        for (final Strategy strategy : strategies) {
            if (strategy.getBomToolType() == BomToolType.YARN) {
                packageLockStrategy.yieldsTo(strategy);
                shrinkwrapStrategy.yieldsTo(strategy);
                cliStrategy.yieldsTo(strategy);
            }
        }

        cliStrategy.yieldsTo(shrinkwrapStrategy);
        cliStrategy.yieldsTo(packageLockStrategy);

    }
}
