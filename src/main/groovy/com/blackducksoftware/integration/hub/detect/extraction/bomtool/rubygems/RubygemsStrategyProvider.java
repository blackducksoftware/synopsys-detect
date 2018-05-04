package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class RubygemsStrategyProvider extends StrategyProvider {

    public static final String GEMFILE_LOCK_FILENAME= "Gemfile.lock";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy gemlockStrategy = newStrategyBuilder(GemlockContext.class, GemlockExtractor.class)
                .named("Gemlock", BomToolType.RUBYGEMS)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(GEMFILE_LOCK_FILENAME).as((context, file) -> context.gemlock = file)
                .build();

        add(gemlockStrategy);

    }

}
