package com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt;

import java.io.File;

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
public class SbtResolutionCacheStrategy extends Strategy<SbtResolutionCacheContext, SbtResolutionCacheExtractor> {
    public static final String BUILD_SBT_FILENAME = "build.sbt";

    @Autowired
    public DetectFileFinder fileFinder;

    public SbtResolutionCacheStrategy() {
        super("Build SBT", BomToolType.SBT, SbtResolutionCacheContext.class, SbtResolutionCacheExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final SbtResolutionCacheContext context) {
        final File build = fileFinder.findFile(environment.getDirectory(), BUILD_SBT_FILENAME);
        if (build == null) {
            return new FileNotFoundStrategyResult(BUILD_SBT_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final SbtResolutionCacheContext context){
        return new PassedStrategyResult();
    }

}