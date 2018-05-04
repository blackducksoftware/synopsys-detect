package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class PackagistStrategyProvider extends StrategyProvider {

    public static final String COMPOSER_LOCK = "composer.lock";
    public static final String COMPOSER_JSON = "composer.json";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy pomStrategy = newStrategyBuilder(ComposerLockContext.class, ComposerLockExtractor.class)
                .named("Composer Lock", BomToolType.PACKAGIST)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(COMPOSER_JSON).as((context, file) -> context.composerJson = file)
                .needsFile(COMPOSER_LOCK).as((context, file) -> context.composerLock = file)
                .build();

        add(pomStrategy);

    }

}
