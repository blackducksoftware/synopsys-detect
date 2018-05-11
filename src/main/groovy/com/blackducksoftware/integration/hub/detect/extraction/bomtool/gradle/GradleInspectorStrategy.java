package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

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
public class GradleInspectorStrategy extends Strategy<GradleInspectorContext, GradleInspectorExtractor> {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public GradleExecutableFinder gradleFinder;

    @Autowired
    public GradleInspectorManager gradleInspectorManager;


    public GradleInspectorStrategy() {
        super("Gradle Inspector", BomToolType.GRADLE, GradleInspectorContext.class, GradleInspectorExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final GradleInspectorContext context) {
        final File buildGradle = fileFinder.findFile(evaluation.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle == null) {
            return Applicable.doesNotApply("No build gradle was found with pattern: " + BUILD_GRADLE_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final GradleInspectorContext context){

        context.gradleExe = gradleFinder.findGradle(evaluation);
        if (context.gradleExe == null) {
            return Extractable.canNotExtract("No gradle executable was found.");
        }

        context.gradleInspector = gradleInspectorManager.getGradleInspector(evaluation);
        if (context.gradleInspector == null) {
            return Extractable.canNotExtract("No gradle inspector was found.");
        }

        return Extractable.canExtract();
    }

}