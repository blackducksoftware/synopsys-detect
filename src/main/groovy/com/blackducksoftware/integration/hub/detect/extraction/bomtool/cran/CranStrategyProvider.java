package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class CranStrategyProvider  extends StrategyProvider {

    public static final String PACKRATLOCK = "packrat.lock";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy packratStrategy = newStrategyBuilder(PackratLockContext.class, PackratLockExtractor.class)
                .named("Packrat Lock", BomToolType.CRAN)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PACKRATLOCK).as((context, file) -> context.packratlock = file)
                .build();

        add(packratStrategy);

    }

}
