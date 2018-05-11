package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class PocklockRqStrategy extends Strategy<PodlockContext, PodlockExtractor> {
    public static final String PODFILE_LOCK_FILENAME = "Podfile.lock";
    private final FileRequirement podlockReq;

    public PocklockRqStrategy() {
        super("Podlock", BomToolType.COCOAPODS, PodlockContext.class, PodlockExtractor.class);

        podlockReq = needsFile(PODFILE_LOCK_FILENAME);
    }

    public void inject(final StrategyEvaluation evaluation, final PodlockContext context) {
        context.podlock = evaluation.getValue(podlockReq);
    }

}
