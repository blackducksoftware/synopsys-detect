package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class PocklockStrategy extends Strategy<PodlockContext, PodlockExtractor> {
    public static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public PocklockStrategy() {
        super("Podlock", BomToolType.COCOAPODS, PodlockContext.class, PodlockExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final PodlockContext context) {
        context.podlock = fileFinder.findFile(evaluation.getDirectory(), PODFILE_LOCK_FILENAME);
        if (context.podlock == null) {
            return Applicable.doesNotApply("No podlock file was found with pattern: " + PODFILE_LOCK_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final PodlockContext context){
        return Extractable.canExtract();
    }

}
