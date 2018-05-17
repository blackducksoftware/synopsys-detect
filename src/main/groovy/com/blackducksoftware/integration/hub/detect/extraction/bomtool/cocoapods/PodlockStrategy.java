package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cocoapods;

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
public class PodlockStrategy extends Strategy<PodlockContext, PodlockExtractor> {
    public static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public PodlockStrategy() {
        super("Podlock", BomToolType.COCOAPODS, PodlockContext.class, PodlockExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final PodlockContext context) {
        context.podlock = fileFinder.findFile(environment.getDirectory(), PODFILE_LOCK_FILENAME);
        if (context.podlock == null) {
            return new FileNotFoundStrategyResult(PODFILE_LOCK_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final PodlockContext context){
        return new PassedStrategyResult();
    }


}
