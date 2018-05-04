package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class CocoapodsStrategyProvider extends StrategyProvider {

    public static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy podlockStrategy = newStrategyBuilder(PodlockContext.class, PodlockExtractor.class)
                .named("Podlock", BomToolType.COCOAPODS)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(PODFILE_LOCK_FILENAME).as((context, file) -> context.podlock = file)
                .build();


        add(podlockStrategy);

    }

}