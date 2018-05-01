package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.GradleExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.GradleInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class GradleStrategyProvider extends StrategyProvider {

    static final String BUILD_GRADLE_FILENAME = "build.gradle";

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy cpanCliStrategy = newStrategyBuilder(GradleContext.class, GradleExtractor.class)
                .needsBomTool(BomToolType.CRAN).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(BUILD_GRADLE_FILENAME).noop()
                .demands(new GradleExecutableRequirement(), (context, file) -> context.gradleExe = file)
                .demands(new GradleInspectorRequirement(), (context, file) -> context.gradleInspector = file)
                .build();

        return Arrays.asList(cpanCliStrategy);

    }

}
