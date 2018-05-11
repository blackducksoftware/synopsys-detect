package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NpmPackageLockStrategy extends Strategy<NpmLockfileContext, NpmLockfileExtractor> {
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";

    @Autowired
    public DetectFileFinder fileFinder;

    public NpmPackageLockStrategy() {
        super("Package Lock", BomToolType.NPM, NpmLockfileContext.class, NpmLockfileExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final NpmLockfileContext context) {
        context.lockfile = fileFinder.findFile(evaluation.getDirectory(), PACKAGE_LOCK_JSON);
        if (context.lockfile == null) {
            return Applicable.doesNotApply("No package lock file was found with pattern: " + PACKAGE_LOCK_JSON);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final NpmLockfileContext context){
        return Extractable.canExtract();
    }

}