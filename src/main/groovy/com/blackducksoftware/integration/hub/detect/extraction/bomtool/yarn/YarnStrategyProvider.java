package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class YarnStrategyProvider extends StrategyProvider {

    public static final String YARN_LOCK_FILENAME= "yarn.lock";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy yarnlockStrategy = newStrategyBuilder(YarnLockContext.class, YarnLockExtractor.class)
                .named("Yarn Lock", BomToolType.YARN)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(YARN_LOCK_FILENAME).as((context, file) -> context.yarnlock = file)
                .build();

        add(yarnlockStrategy);

    }

}
