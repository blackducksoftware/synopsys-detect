package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NpmShrinkwrapStrategy extends Strategy<NpmLockfileContext, NpmLockfileExtractor> {
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    @Autowired
    public DetectFileFinder fileFinder;

    public NpmShrinkwrapStrategy() {
        super("Shrinkwrap", BomToolType.NPM, NpmLockfileContext.class, NpmLockfileExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final NpmLockfileContext context) {
        context.lockfile = fileFinder.findFile(environment.getDirectory(), SHRINKWRAP_JSON);
        if (context.lockfile == null) {
            return new FileNotFoundStrategyResult(SHRINKWRAP_JSON);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final NpmLockfileContext context){
        return new PassedStrategyResult();
    }

}