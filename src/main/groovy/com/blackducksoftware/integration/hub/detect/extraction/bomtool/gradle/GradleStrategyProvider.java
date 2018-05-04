package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.GradleExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.GradleInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class GradleStrategyProvider extends StrategyProvider {

    static final String BUILD_GRADLE_FILENAME = "build.gradle";

    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy gradleStrategy = newStrategyBuilder(GradleInspectorContext.class, GradleInspectorExtractor.class)
                .named("Gradle Inspector", BomToolType.GRADLE)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(BUILD_GRADLE_FILENAME).noop()
                .demands(new GradleExecutableRequirement(), (context, file) -> context.gradleExe = file)
                .demands(new GradleInspectorRequirement(), (context, file) -> context.gradleInspector = file)
                .build();

        add(gradleStrategy);

    }

}
