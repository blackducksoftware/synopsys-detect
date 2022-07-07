package com.synopsys.integration.detectable.detectables.gradle.parsing;

import java.io.IOException;
import java.util.Collections;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorExtractor;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Gradle Project Inspector", language = "various", forge = "Maven Central", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: build.gradle")
public class GradleProjectInspectorDetectable extends Detectable {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    private final FileFinder fileFinder;
    private final ProjectInspectorResolver projectInspectorResolver;
    private final ProjectInspectorExtractor projectInspectorExtractor;
    private final ProjectInspectorOptions projectInspectorOptions;

    private ExecutableTarget inspector;

    public GradleProjectInspectorDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        ProjectInspectorResolver projectInspectorResolver,
        ProjectInspectorExtractor projectInspectorExtractor,
        ProjectInspectorOptions projectInspectorOptions
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.projectInspectorResolver = projectInspectorResolver;
        this.projectInspectorExtractor = projectInspectorExtractor;
        this.projectInspectorOptions = projectInspectorOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(BUILD_GRADLE_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        inspector = requirements.executable(projectInspectorResolver::resolveProjectInspector, "Project Inspector");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return projectInspectorExtractor.extract(
            projectInspectorOptions,
            Collections.emptyList(),
            environment.getDirectory(),
            extractionEnvironment.getOutputDirectory(),
            inspector
        );
    }

}