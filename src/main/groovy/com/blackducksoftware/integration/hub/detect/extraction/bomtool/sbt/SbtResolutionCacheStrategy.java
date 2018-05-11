package com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
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

    public Applicable applicable(final EvaluationContext evaluation, final SbtResolutionCacheContext context) {
        final File build = fileFinder.findFile(evaluation.getDirectory(), BUILD_SBT_FILENAME);
        if (build == null) {
            return Applicable.doesNotApply("No build file was found with pattern: " + BUILD_SBT_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final SbtResolutionCacheContext context){
        return Extractable.canExtract();
    }

}