package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
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

    public Applicable applicable(final EvaluationContext evaluation, final ComposerLockContext context) {
        context.composerLock = fileFinder.findFile(evaluation.getDirectory(), COMPOSER_LOCK);
        if (context.composerLock == null) {
            return Applicable.doesNotApply("No composer lock file was found with pattern: " + COMPOSER_LOCK);
        }

        context.composerJson = fileFinder.findFile(evaluation.getDirectory(), COMPOSER_JSON);
        if (context.composerJson == null) {
            return Applicable.doesNotApply("No composer json file was found with pattern: " + COMPOSER_JSON);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final ComposerLockContext context){
        return Extractable.canExtract();
    }

}