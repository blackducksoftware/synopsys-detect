package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

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
public class ComposerLockStrategy extends Strategy<ComposerLockContext, ComposerLockExtractor> {
    public static final String COMPOSER_LOCK = "composer.lock";
    public static final String COMPOSER_JSON = "composer.json";

    @Autowired
    public DetectFileFinder fileFinder;

    public ComposerLockStrategy() {
        super("Composer Lock", BomToolType.PACKAGIST, ComposerLockContext.class, ComposerLockExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final ComposerLockContext context) {
        context.composerLock = fileFinder.findFile(environment.getDirectory(), COMPOSER_LOCK);
        if (context.composerLock == null) {
            return new FileNotFoundStrategyResult(COMPOSER_LOCK);
        }

        context.composerJson = fileFinder.findFile(environment.getDirectory(), COMPOSER_JSON);
        if (context.composerJson == null) {
            return new FileNotFoundStrategyResult(COMPOSER_JSON);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final ComposerLockContext context){
        return new PassedStrategyResult();
    }

}