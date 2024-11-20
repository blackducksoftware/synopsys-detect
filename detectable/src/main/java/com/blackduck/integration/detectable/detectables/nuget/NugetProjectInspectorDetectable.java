package com.blackduck.integration.detectable.detectables.nuget;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectables.projectinspector.ProjectInspectorExtractor;
import com.blackduck.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "NuGet Project Inspector", language = "C#", forge = "NuGet.org", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: a project file with one of the following extensions: .csproj, .sln")
public class NugetProjectInspectorDetectable extends Detectable {
    static final List<String> SUPPORTED_PROJECT_PATTERNS = Arrays.asList("*.csproj", "*.sln");

    private final FileFinder fileFinder;
    private final ProjectInspectorResolver projectInspectorResolver;
    private final ProjectInspectorExtractor projectInspectorExtractor;
    private final ProjectInspectorOptions projectInspectorOptions;

    private ExecutableTarget inspector;

    public NugetProjectInspectorDetectable(
        DetectableEnvironment detectableEnvironment,
        FileFinder fileFinder,
        ProjectInspectorResolver projectInspectorResolver,
        ProjectInspectorExtractor projectInspectorExtractor,
        ProjectInspectorOptions projectInspectorOptions
    ) {
        super(detectableEnvironment);
        this.fileFinder = fileFinder;
        this.projectInspectorResolver = projectInspectorResolver;
        this.projectInspectorExtractor = projectInspectorExtractor;
        this.projectInspectorOptions = projectInspectorOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.anyFileMatchesPatterns(SUPPORTED_PROJECT_PATTERNS);
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