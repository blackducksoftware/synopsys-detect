package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class PackratLockStrategy extends Strategy<PackratLockContext, PackratLockExtractor> {
    public static final String PACKRATLOCK = "packrat.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public PackratLockStrategy() {
        super("Packrat Lock", BomToolType.CRAN, PackratLockContext.class, PackratLockExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final PackratLockContext context) {
        context.packratlock = fileFinder.findFile(evaluation.getDirectory(), PACKRATLOCK);
        if (context.packratlock == null) {
            return Applicable.doesNotApply("No packrat file was found with pattern: " + PACKRATLOCK);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final PackratLockContext context){
        return Extractable.canExtract();
    }

}
