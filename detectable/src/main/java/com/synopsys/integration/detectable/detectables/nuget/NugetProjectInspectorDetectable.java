/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.nuget;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.explanation.FoundInspector;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "C#", forge = "NuGet.org", requirementsMarkdown = "File: a project file with one of the following extensions: .csproj, .sln")
public class NugetProjectInspectorDetectable extends Detectable {
    static final List<String> SUPPORTED_PROJECT_PATTERNS = Arrays.asList("*.csproj", "*.sln");

    private final FileFinder fileFinder;
    private final NugetInspectorOptions nugetInspectorOptions;
    private final ProjectInspectorResolver projectInspectorResolver;
    private final NugetProjectInspectorExtractor nugetProjectInspectorExtractor;

    private ExecutableTarget inspector;
    private List<File> projectFiles = new ArrayList<>();

    public NugetProjectInspectorDetectable(final DetectableEnvironment detectableEnvironment, final FileFinder fileFinder, final NugetInspectorOptions nugetInspectorOptions,
        ProjectInspectorResolver projectInspectorResolver, NugetProjectInspectorExtractor nugetProjectInspectorExtractor) {
        super(detectableEnvironment);
        this.fileFinder = fileFinder;
        this.nugetInspectorOptions = nugetInspectorOptions;
        this.projectInspectorResolver = projectInspectorResolver;
        this.nugetProjectInspectorExtractor = nugetProjectInspectorExtractor;
    }

    @Override
    public DetectableResult applicable() {
        projectFiles = fileFinder.findFiles(environment.getDirectory(), SUPPORTED_PROJECT_PATTERNS);

        if (projectFiles != null && projectFiles.size() > 0) {
            PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
            projectFiles.forEach(passedResultBuilder::foundFile);
            return passedResultBuilder.build();
        } else {
            return new FilesNotFoundDetectableResult(SUPPORTED_PROJECT_PATTERNS);
        }
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        inspector = projectInspectorResolver.resolveProjectInspector();

        if (inspector == null) {
            return new InspectorNotFoundDetectableResult("Project Inspector");
        }

        return new PassedDetectableResult(new FoundInspector("Project Inspector"));
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return nugetProjectInspectorExtractor.extract(environment.getDirectory(), extractionEnvironment.getOutputDirectory(), inspector, nugetInspectorOptions);
    }

}