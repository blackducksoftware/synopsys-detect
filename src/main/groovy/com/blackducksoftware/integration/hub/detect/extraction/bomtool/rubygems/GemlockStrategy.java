package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GemlockStrategy extends Strategy<GemlockContext, GemlockExtractor> {
    public static final String GEMFILE_LOCK_FILENAME = "Gemfile.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public GemlockStrategy() {
        super("Gemlock", BomToolType.RUBYGEMS, GemlockContext.class, GemlockExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final GemlockContext context) {
        context.gemlock = fileFinder.findFile(environment.getDirectory(), GEMFILE_LOCK_FILENAME);
        if (context.gemlock == null) {
            return new FileNotFoundStrategyResult(GEMFILE_LOCK_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final GemlockContext context){
        return new PassedStrategyResult();
    }

}