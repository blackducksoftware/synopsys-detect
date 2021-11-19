/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.explanation.FoundExecutable;
import com.synopsys.integration.detectable.detectable.explanation.FoundFile;
import com.synopsys.integration.detectable.detectable.explanation.FoundInspector;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.*;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@DetectableInfo(language = "various", forge = "Maven Central", requirementsMarkdown = "File: build.gradle or build.gradle.kts.<br/><br/>Executable: gradlew or gradle.")
public class GradleDetectable extends Detectable {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";
    public static final String KOTLIN_BUILD_GRADLE_FILENAME = "build.gradle.kts";

    private final FileFinder fileFinder;
    private final GradleResolver gradleResolver;
    private final GradleInspectorResolver gradleInspectorResolver;
    private final GradleInspectorExtractor gradleInspectorExtractor;
    private final GradleInspectorOptions gradleInspectorOptions;

    private ExecutableTarget gradleExe;
    private File gradleInspector;

    public GradleDetectable(DetectableEnvironment environment, FileFinder fileFinder, GradleResolver gradleResolver, GradleInspectorResolver gradleInspectorResolver,
        GradleInspectorExtractor gradleInspectorExtractor, GradleInspectorOptions gradleInspectorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gradleResolver = gradleResolver;
        this.gradleInspectorResolver = gradleInspectorResolver;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
        this.gradleInspectorOptions = gradleInspectorOptions;
    }

    @Override
    public DetectableResult applicable() {
        File buildGradle = fileFinder.findFile(environment.getDirectory(), BUILD_GRADLE_FILENAME);
        if (buildGradle != null) {
            return new PassedDetectableResult(new FoundFile(buildGradle));
        }

        File kotlinBuildGradle = fileFinder.findFile(environment.getDirectory(), KOTLIN_BUILD_GRADLE_FILENAME);
        if (kotlinBuildGradle != null) {
            return new PassedDetectableResult(new FoundFile(kotlinBuildGradle));
        }

        return new FilesNotFoundDetectableResult(BUILD_GRADLE_FILENAME, KOTLIN_BUILD_GRADLE_FILENAME);
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        List<Explanation> explanations = new ArrayList<>();
        gradleExe = gradleResolver.resolveGradle(environment);
        if (gradleExe == null) {
            return new ExecutableNotFoundDetectableResult("gradle");
        } else {
            explanations.add(new FoundExecutable(gradleExe));
        }

        gradleInspector = gradleInspectorResolver.resolveGradleInspector();
        if (gradleInspector == null) {
            return new InspectorNotFoundDetectableResult("gradle");
        } else {
            explanations.add(new FoundInspector(gradleInspector));
        }

        return new PassedDetectableResult(explanations);
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        String gradleCommand = gradleInspectorOptions.getGradleBuildCommand().orElse(null);
        return gradleInspectorExtractor.extract(environment.getDirectory(), gradleExe, gradleCommand, gradleInspectorOptions.getproxyInfo(), gradleInspector, extractionEnvironment.getOutputDirectory());
    }
}
