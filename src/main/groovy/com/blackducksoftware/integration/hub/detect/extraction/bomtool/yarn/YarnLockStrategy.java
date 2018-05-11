package com.blackducksoftware.integration.hub.detect.extraction.bomtool.yarn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class YarnLockStrategy extends Strategy<YarnLockContext, YarnLockExtractor> {
    public static final String YARN_LOCK_FILENAME = "yarn.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public YarnLockStrategy() {
        super("Yarn Lock", BomToolType.YARN, YarnLockContext.class, YarnLockExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final YarnLockContext context) {
        context.yarnlock = fileFinder.findFile(evaluation.getDirectory(), YARN_LOCK_FILENAME);
        if (context.yarnlock == null) {
            return Applicable.doesNotApply("No podlock file was found with pattern: " + YARN_LOCK_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final YarnLockContext context){
        return Extractable.canExtract();
    }

}