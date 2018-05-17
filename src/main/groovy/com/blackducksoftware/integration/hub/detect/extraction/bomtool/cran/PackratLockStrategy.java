package com.blackducksoftware.integration.hub.detect.extraction.bomtool.cran;

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
public class PackratLockStrategy extends Strategy<PackratLockContext, PackratLockExtractor> {
    public static final String PACKRATLOCK = "packrat.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public PackratLockStrategy() {
        super("Packrat Lock", BomToolType.CRAN, PackratLockContext.class, PackratLockExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final PackratLockContext context) {
        context.packratlock = fileFinder.findFile(environment.getDirectory(), PACKRATLOCK);
        if (context.packratlock == null) {
            return new FileNotFoundStrategyResult(PACKRATLOCK);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final PackratLockContext context){
        return new PassedStrategyResult();
    }

}
