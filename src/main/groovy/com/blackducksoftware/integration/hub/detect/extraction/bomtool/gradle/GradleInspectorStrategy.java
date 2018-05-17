package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.extraction.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GradleInspectorStrategy extends Strategy<GradleInspectorContext, GradleInspectorExtractor> {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public GradleExecutableFinder gradleFinder;

    @Autowired
    public GradleInspectorManager gradleInspectorManager;

    @Autowired
    public GradleInspectorExtractor gradleInspectorExtractor;

    public GradleInspectorStrategy() {
        super("Gradle Inspector", BomToolType.GRADLE, GradleInspectorContext.class, GradleInspectorExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final GradleInspectorContext context) {
        final File buildGradle = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle == null) {
            return new FileNotFoundStrategyResult(BUILD_GRADLE_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final GradleInspectorContext context) throws StrategyException {
        context.gradleExe = gradleFinder.findGradle(environment);
        if (context.gradleExe == null) {
            return new ExecutableNotFoundStrategyResult("gradle");
        }

        context.gradleInspector = gradleInspectorManager.getGradleInspector(environment);
        if (context.gradleInspector == null) {
            return new InspectorNotFoundStrategyResult("gradle");
        }

        return new PassedStrategyResult();
    }

    public Extractor<GradleInspectorContext> getExtractor() throws IOException  {
        return gradleInspectorExtractor;
    }

}