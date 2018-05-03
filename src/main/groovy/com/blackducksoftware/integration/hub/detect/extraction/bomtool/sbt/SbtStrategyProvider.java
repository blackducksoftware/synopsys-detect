package com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class SbtStrategyProvider extends StrategyProvider {

    static final String BUILD_SBT_FILENAME = "build.sbt";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy gemlockStrategy = newStrategyBuilder(SbtResolutionCacheContext.class, SbtResolutionCacheExtractor.class)
                .needsBomTool(BomToolType.RUBYGEMS).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(BUILD_SBT_FILENAME).noop()
                .build();

        add(gemlockStrategy);

    }

}
