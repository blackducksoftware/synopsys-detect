package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran;

import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CranStrategyProvider  extends StrategyProvider {

    public static final String PACKRATLOCK = "packrat.lock";

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy cpanCliStrategy = newStrategyBuilder(PackratLockContext.class, PackratLockExtractor.class)
                .needsBomTool(BomToolType.CRAN).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKRATLOCK).as((context, file) -> context.packratlock = file)
                .build();

        return Arrays.asList(cpanCliStrategy);

    }

}
